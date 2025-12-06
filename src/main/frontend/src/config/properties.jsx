export const appTitle = "LikeLive";
export const contactInfo = "Xアカウント:@river_tensix";
export const copyRight = "© 2024 river";
export const localUri = "http://localhost:8080";
const dev = {
  apiBaseUrl: "http://localhost:8080", // ローカル開発環境のAPIベースURL
  oauthRedirectUri: "http://localhost:3000/login/callback",
  getImageUrl: "http://localhost:8080/api/public/files",
};

const prod = {
  apiBaseUrl: "https://likelive.life:443", // 本番環境のAPIベースURL
  oauthRedirectUri: "https://likelive.xyz:443/",
  getImageUrl: "https://d3n24a5r1zq3yp.cloudfront.net/uploads",
};

const config = process.env.NODE_ENV === "development" ? dev : prod;

export default config;
