import { useNavigate } from "react-router-dom";

function LoginButton({ onLogin }) {
  const navigate = useNavigate();

  return (
    <>
      {onLogin ? (
        <button
          className="px-4 py-2 text-sm font-medium text-white bg-transparent border border-white rounded-md hover:bg-white hover:text-black focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-white"
          onClick={() => onLogin()}
        >
          サインイン
        </button>
      ) : (
        <button
          className="px-4 py-2 text-sm font-medium text-white bg-transparent border border-white rounded-md hover:bg-white hover:text-black focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-white"
          onClick={() => navigate("/")}
        >
          新規登録
        </button>
      )}
    </>
  );
}

export default LoginButton;
