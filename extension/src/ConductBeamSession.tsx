import React, { useEffect } from "react";
import { QRCodeSVG } from "qrcode.react";

// const SERVER_URL = "https://beamborg.fly.dev";
const SERVER_URL = "http://192.168.0.225:8080";

type BeamSession = { id: string; content?: string };

const createNewSession = async () => {
  const response = await fetch(`${SERVER_URL}/api/v1/session/new`, {
    method: "POST",
  });
  const asJson: BeamSession = await response.json();
  return asJson.id;
};

const getBeamSessionUrl = (sessionId: string) =>
  `${SERVER_URL}/api/v1/session/${sessionId}`;

const getBeamSessionUIUrl = (sessionId: string) =>
  `${SERVER_URL}/session/${sessionId}`;

const fetchBeamSession = async (sessionId: string): Promise<BeamSession> => {
  const response = await fetch(getBeamSessionUrl(sessionId));
  const asJson: BeamSession = await response.json();
  return asJson;
};

export const ConductBeamSession: React.FC<{
  setTextClipboardItem: (text: string) => void;
  setBlobClipboardItem: (blob: Blob) => void;
}> = ({ setTextClipboardItem }) => {
  const interval = React.useRef<number | null>(null);
  const [sessionId, setSessionId] = React.useState<string | null>(null);
  const onStart = async () => {
    const newSessionId = await createNewSession();
    setSessionId(newSessionId);
  };

  const pollBeamSession = async () => {
    if (sessionId) {
      const beamSession = await fetchBeamSession(sessionId);
      if (beamSession.content) {
        if (interval.current) {
          clearInterval(interval.current);
        }
        setTextClipboardItem(beamSession.content);
      }
    }
  };

  useEffect(() => {
    onStart();
  }, []);

  useEffect(() => {
    if (interval.current) {
      clearInterval(interval.current);
    }
    if (sessionId) {
      interval.current = setInterval(pollBeamSession, 500);
    }
  }, [sessionId]);

  const sessionUIUrl = sessionId ? getBeamSessionUIUrl(sessionId) : "";
  return (
    <div>
      {sessionId && (
        <>
          <QRCodeSVG value={sessionUIUrl} />
          <p>
            <a href={sessionUIUrl}>{sessionUIUrl}</a>
          </p>
          <p>{sessionId}</p>
        </>
      )}
    </div>
  );
};
