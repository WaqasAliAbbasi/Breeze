export const getCatFact = async (): Promise<string> => {
  const catFactResponse = await fetch("https://catfact.ninja/fact");
  const catFactResponseJson = await catFactResponse.json();
  return catFactResponseJson.fact;
};

export const getImageBlobFromUrl = async (url: string): Promise<Blob> => {
  const fetchedImageData = await fetch(url);
  const blob = await fetchedImageData.blob();
  return blob;
};
