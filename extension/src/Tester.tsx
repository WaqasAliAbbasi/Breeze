import React from "react";
import { getCatFact, getImageBlobFromUrl } from "./services";

export const Tester: React.FC<{
  setTextClipboardItem: (text: string) => void;
  setBlobClipboardItem: (blob: Blob) => void;
}> = ({ setTextClipboardItem, setBlobClipboardItem }) => {
  const fetchCatFact = async () => setTextClipboardItem(await getCatFact());

  const fetchImage = async () => {
    const imageBlob = await getImageBlobFromUrl(
      "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/1920px-Clojure_logo.svg.png"
    );
    setBlobClipboardItem(imageBlob);
  };

  return (
    <div>
      <button onClick={() => fetchCatFact()}>Fetch Cat Fact</button>
      <button onClick={() => fetchImage()}>Fetch Image</button>
    </div>
  );
};
