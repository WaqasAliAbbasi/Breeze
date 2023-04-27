import React from "react";
import "./App.css";
import { useParams } from "react-router-dom";

const BEAMBORG_SERVER = "/api/v1";

type BeamSession = { id: string; content?: string };

const getBeamSessionUrl = (sessionId: string) =>
  `${BEAMBORG_SERVER}/session/${sessionId}`;

const fetchBeamSession = async (sessionId: string): Promise<BeamSession> => {
  const response = await fetch(getBeamSessionUrl(sessionId));
  const asJson: BeamSession = await response.json();
  return asJson;
};

const updateBeamSessionContent = async (
  sessionId: string,
  formData: FormData
): Promise<string> => {
  const response = await fetch(
    `${BEAMBORG_SERVER}/session/${sessionId}/upload`,
    {
      method: "POST",
      body: formData,
    }
  );
  const textResponse = await response.text();
  return textResponse;
};

export const SessionPage = () => {
  const { sessionId } = useParams();
  const [beamSession, setBeamSession] = React.useState<BeamSession | null>(
    null
  );
  const [textData, setTextData] = React.useState<string | null>(null);
  const [image, setImage] = React.useState<File | null>(null);

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

  return (
    <>
      <h1>BeamBorg</h1>
      <div className="card">
        <p>{JSON.stringify(beamSession).slice(0, 100)}</p>
        <textarea
          rows={4}
          cols={50}
          value={textData || ""}
          onChange={(event) => setTextData(event.target.value)}
        ></textarea>
        <button onClick={updateBeamSessionText}>Beam Text</button>
        <br />
        <input
          type="file"
          id="img"
          name="img"
          accept="image/png"
          onChange={onSelectFile}
        ></input>
        <button onClick={updateBeamSessionImage}>Beam Image</button>
      </div>
    </>
  );
};
