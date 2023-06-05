import { useState } from "react";
import { ConductBeamSession } from "./ConductBeamSession";
import { Button } from "./components/Button";

function App() {
  const [data, setData] = useState<ClipboardItem | null>(null);
  const [dataAsBlob, setDataAsBlob] = useState<Blob | null>(null);
  const [dataAsText, setDataAsText] = useState<string | null>(null);

  const setTextClipboardItem = (text: string) => {
    setDataAsText(text);
    setData(
      new ClipboardItem({
        "text/plain": new Blob([text], { type: "text/plain" }),
      })
    );
  };

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
    <div className="bg-gray-100">
      <header className="bg-white shadow-sm">
        <div className="mx-auto max-w-7xl px-6 py-4">
          <h1 className="text-lg font-semibold leading-6 text-gray-900">
            Breeze
          </h1>
        </div>
      </header>
      <main className="mx-auto max-w-7xl flex flex-col gap-4 items-center py-6 px-6">
        {!data && (
          <ConductBeamSession
            setBlobClipboardItem={setBlobClipboardItem}
            setTextClipboardItem={setTextClipboardItem}
          />
        )}
        {dataAsText && <p>{dataAsText}</p>}
        {dataAsBlob && (
          <img src={URL.createObjectURL(dataAsBlob)} width="80%" />
        )}
        <Button onClick={() => onCopyAndClose()} disabled={!data}>
          Copy and Close
        </Button>
      </main>
    </div>
  );
}

export default App;
