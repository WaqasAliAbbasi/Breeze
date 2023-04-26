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
  text: string
): Promise<BeamSession> => {
  const response = await fetch(getBeamSessionUrl(sessionId), {
    method: "PATCH",
    body: JSON.stringify({ content: text }),
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
  });
  const asJson: BeamSession = await response.json();
  return asJson;
};

export const SessionPage = () => {
  const { sessionId } = useParams();
  const [beamSession, setBeamSession] = React.useState<BeamSession | null>(
    null
  );
  const [textData, setTextData] = React.useState<string | null>(null);

  const updateBeamSession = () => {
    if (sessionId && textData) {
      updateBeamSessionContent(sessionId, textData).then(
        (beamSessionResponse) => setBeamSession(beamSessionResponse)
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

  return (
    <>
      <h1>BeamBorg</h1>
      <div className="card">
        <p>{JSON.stringify(beamSession)}</p>
        <textarea
          rows={4}
          cols={50}
          value={textData || ""}
          onChange={(event) => setTextData(event.target.value)}
        ></textarea>
        <button onClick={updateBeamSession}>Beam</button>
      </div>
    </>
  );
};
