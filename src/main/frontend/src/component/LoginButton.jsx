// import { useGoogleLogin } from "@react-oauth/google";

function LoginButton({ onLogin, setIsAuthenticated }) {
  // const login = useGoogleLogin({
  //   onSuccess: async (tokenResponse) => {},
  //   flow: "auth-code", // リダイレクト方式を指定
  //   ux_mode: "redirect",
  //   redirect_uri: "http://localhost:3000/login/callback", // リダイレクト先を指定

  //   onError: (errorResponse) => {
  //     console.log(errorResponse);
  //     localStorage.setItem("ll_isAuthenticated", "false");
  //     localStorage.removeItem("ll_accessToken");
  //   },
  // });

  return (
    <>
      <button
        className="px-4 py-2 text-sm font-medium text-white bg-transparent border border-white rounded-md hover:bg-white hover:text-black focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-white"
        onClick={() => onLogin()}
      >
        サインイン
      </button>
    </>
  );
}

export default LoginButton;
