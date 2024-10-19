export const getCroppedImg = (imageSrc, crop) => {
  const createImage = (url) =>
    new Promise((resolve, reject) => {
      const image = new Image();
      image.addEventListener("load", () => resolve(image));
      image.addEventListener("error", (error) => reject(error));
      image.setAttribute("crossOrigin", "anonymous"); // CORS対応
      image.src = url;
    });

  return new Promise(async (resolve, reject) => {
    const image = await createImage(imageSrc);
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");

    const aspectRatio = image.width / image.height;
    const size = Math.min(image.width, image.height);

    canvas.width = size;
    canvas.height = size;
    if (
      !crop ||
      typeof crop.x !== "number" ||
      typeof crop.y !== "number" ||
      typeof crop.width !== "number" ||
      typeof crop.height !== "number"
    ) {
      return reject(new Error("Invalid crop parameters"));
    }

    ctx.drawImage(
      image,
      crop.x,
      crop.y,
      crop.width,
      crop.height,
      0,
      0,
      canvas.width,
      canvas.height
    );

    canvas.toBlob((blob) => {
      if (!blob) {
        return reject(new Error("Canvas toBlob failed"));
      }
      const fileUrl = URL.createObjectURL(blob);
      resolve(fileUrl);
    }, "image/jpeg");
  });
};
