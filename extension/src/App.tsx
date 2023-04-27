import { useState } from "react";
import "./App.css";
import { ConductBeamSession } from "./ConductBeamSession";

function App() {
  const [data, setData] = useState<ClipboardItem | null>(null);
  const [dataAsBlob, setDataAsBlob] = useState<Blob | null>(null);

  const setTextClipboardItem = (text: string) =>
    setData(
      new ClipboardItem({
        "text/plain": new Blob([text], { type: "text/plain" }),
      })
    );

  const setBlobClipboardItem = (blob: Blob) => {
    setDataAsBlob(blob);
    setData(
      new ClipboardItem({
        [blob.type]: blob,
      })
    );
  };

  const onCopyAndClose = () => {
    if (data) {
      navigator.clipboard.write([data]).then(
        () => {
          console.log("Data Successfully copied to clipboard");
          window.close();
        },
        (error) => {
          console.error("Error while copying to keyboard", error);
        }
      );
    }
  };

  return (
    <>
      <h1>BeamBorg</h1>
      <ConductBeamSession
        setBlobClipboardItem={setBlobClipboardItem}
        setTextClipboardItem={setTextClipboardItem}
      />
      {data && <h3>Data Fetched</h3>}
      {data && <p>Type: {data.types}</p>}
      {data && dataAsBlob && (
        <img src={URL.createObjectURL(dataAsBlob)} width="80%" />
      )}
      <div className="card">
        <button onClick={() => onCopyAndClose()} disabled={!data}>
          Copy and Close
        </button>
      </div>
    </>
  );
}

export default App;
