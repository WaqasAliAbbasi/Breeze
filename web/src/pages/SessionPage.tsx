import React from "react";
import { useParams } from "react-router-dom";
import { Excalidraw, exportToBlob } from "@excalidraw/excalidraw";
import { ExcalidrawAPIRefValue } from "@excalidraw/excalidraw/types/types";
import { Button } from "../components/Button";

const BREEZE_SERVER = "/api/v1";

type BeamSession = { id: string; content?: string };

const getBeamSessionUrl = (sessionId: string) =>
  `${BREEZE_SERVER}/session/${sessionId}`;

const fetchBeamSession = async (sessionId: string): Promise<BeamSession> => {
  const response = await fetch(getBeamSessionUrl(sessionId));
  const asJson: BeamSession = await response.json();
  return asJson;
};

const updateBeamSessionContent = async (
  sessionId: string,
  formData: FormData
): Promise<string> => {
  const response = await fetch(`${BREEZE_SERVER}/session/${sessionId}/upload`, {
    method: "POST",
    body: formData,
  });
  const textResponse = await response.text();
  return textResponse;
};

export const SessionPage = () => {
  const { sessionId } = useParams();
  const [type, setType] = React.useState<"text" | "image" | "draw" | null>(
    null
  );
  const [beamSession, setBeamSession] = React.useState<BeamSession | null>(
    null
  );
  const [textData, setTextData] = React.useState<string | null>(null);
  const [image, setImage] = React.useState<Blob | null>(null);
  const [excalidrawAPI, setExcalidrawAPI] =
    React.useState<ExcalidrawAPIRefValue | null>(null);

  const updateBeamSessionText = () => {
    if (sessionId && textData) {
      const formData = new FormData();
      formData.append("text", textData);
      updateBeamSessionContent(sessionId, formData).then(() =>
        fetchBeamSession(sessionId).then((beamSessionResponse) =>
          setBeamSession(beamSessionResponse)
        )
      );
    }
  };

  const updateBeamSessionImage = () => {
    if (sessionId && image) {
      const formData = new FormData();
      formData.append("image", image);
      updateBeamSessionContent(sessionId, formData).then(() =>
        fetchBeamSession(sessionId).then((beamSessionResponse) =>
          setBeamSession(beamSessionResponse)
        )
      );
    }
  };

  React.useEffect(() => {
    if (sessionId) {
      fetchBeamSession(sessionId).then((beamSessionResponse) =>
        setBeamSession(beamSessionResponse)
      );
    }
  }, [sessionId]);

  const onSelectFile: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    if (!event.target.files || event.target.files.length === 0) {
      setImage(null);
      return;
    }

    setImage(event.target.files[0]);
  };

  const beamDrawing = async () => {
    if (!sessionId || !excalidrawAPI?.readyPromise) {
      return;
    }
    const elements = (await excalidrawAPI.readyPromise).getSceneElements();
    if (!elements || !elements.length) {
      return;
    }
    const blob = await exportToBlob({
      elements,
      appState: {
        exportWithDarkMode: false,
      },
      files: (await excalidrawAPI.readyPromise).getFiles(),
      getDimensions: () => {
        return { width: 350, height: 350 };
      },
    });
    const formData = new FormData();
    formData.append("image", blob);
    updateBeamSessionContent(sessionId, formData).then(() =>
      fetchBeamSession(sessionId).then((beamSessionResponse) =>
        setBeamSession(beamSessionResponse)
      )
    );
  };

  return (
    <div>
      <header className="bg-white shadow-sm">
        <div className="mx-auto max-w-7xl px-6 py-4">
          <h1 className="text-lg font-semibold leading-6 text-gray-900">
            Breeze
          </h1>
        </div>
      </header>
      <main className="mx-auto max-w-7xl flex flex-col gap-4 items-center py-6 px-6">
        {beamSession && <p>{JSON.stringify(beamSession).slice(0, 100)}</p>}
        {!type && (
          <>
            <Button onClick={() => setType("text")}>Text</Button>
            <Button onClick={() => setType("image")}>Image</Button>
            <Button onClick={() => setType("draw")}>Drawing</Button>
          </>
        )}
        {type && <Button onClick={() => setType(null)}>Back</Button>}
        {type == "text" && (
          <>
            <textarea
              rows={4}
              cols={50}
              value={textData || ""}
              onChange={(event) => setTextData(event.target.value)}
            ></textarea>
            <Button onClick={updateBeamSessionText}>Beam Text</Button>
          </>
        )}
        {type == "image" && (
          <>
            <input
              type="file"
              id="img"
              name="img"
              accept="image/png"
              onChange={onSelectFile}
            ></input>
            <Button onClick={updateBeamSessionImage}>Beam Image</Button>
          </>
        )}
        {type == "draw" && (
          <>
            <div style={{ height: "500px", width: "500px" }}>
              <Excalidraw ref={(api) => setExcalidrawAPI(api)} />
            </div>
            <Button onClick={beamDrawing}>Beam Drawing</Button>
          </>
        )}
      </main>
    </div>
  );
};
