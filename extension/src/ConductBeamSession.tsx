import React, { useEffect } from "react";
import { QRCodeSVG } from "qrcode.react";

const SERVER_HOSTNAME = import.meta.env.VITE_SERVER_HOSTNAME;

type BeamSession = {
  id: string;
  type?: "Text" | "Image";
  content?: string;
};

const createNewSession = async () => {
  const response = await fetch(`${SERVER_HOSTNAME}/api/v1/session/new`, {
    method: "POST",
  });
  const asJson: BeamSession = await response.json();
  return asJson.id;
};

const getBeamSessionUrl = (sessionId: string) =>
  `${SERVER_HOSTNAME}/api/v1/session/${sessionId}`;

const getBeamSessionUIUrl = (sessionId: string) =>
  `${SERVER_HOSTNAME}/session/${sessionId}`;

const fetchBeamSession = async (sessionId: string): Promise<BeamSession> => {
  const response = await fetch(getBeamSessionUrl(sessionId));
  const asJson: BeamSession = await response.json();
  return asJson;
};

export const ConductBeamSession: React.FC<{
  setTextClipboardItem: (text: string) => void;
  setBlobClipboardItem: (blob: Blob) => void;
}> = ({ setTextClipboardItem, setBlobClipboardItem }) => {
  const interval = React.useRef<number | null>(null);
  const [sessionId, setSessionId] = React.useState<string | null>(null);
  const onStart = async () => {
    const newSessionId = await createNewSession();
    setSessionId(newSessionId);
  };

  const pollBeamSession = async () => {
    if (sessionId) {
      const beamSession = await fetchBeamSession(sessionId);
      if (beamSession.type && beamSession.content) {
        if (interval.current) {
          clearInterval(interval.current);
        }
        if (beamSession.type == "Text") {
          setTextClipboardItem(beamSession.content);
        } else {
          const base64Response = await fetch(
            `data:image/png;base64,${beamSession.content}`
          );
          setBlobClipboardItem(await base64Response.blob());
        }
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
        <a href={sessionUIUrl} title={sessionId}>
          <QRCodeSVG value={sessionUIUrl} />
        </a>
      )}
    </div>
  );
};
