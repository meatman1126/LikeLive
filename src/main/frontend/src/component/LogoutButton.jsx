import React from "react";
import { useNavigate } from "react-router-dom";

// このコンポーネントは不要になりました。
const LogoutButton = ({ setIsAuthenticated, setIsMenuOpen }) => {
  const navigate = useNavigate();

  const handleLogout = (event) => {
    event.preventDefault();

    // 認証状態およびアクセストークンをlocalStorageから削除
    localStorage.setItem("ll_isAuthenticated", "false");
    localStorage.removeItem("ll_accessToken");
    localStorage.removeItem("ll_refreshToken");
    localStorage.removeItem("ll_tokenExpires");

    // 認証状態を更新してfalseに
    setIsAuthenticated(false);

    // サーバー側のログアウト処理が必要な場合はここでリクエストを送る
    // fetch('/api/logout', { method: 'POST' })
    // .then(() => {
    // トップページやログインページにリダイレクト
    setIsMenuOpen(false);
    navigate("/");
    // });
  };

  return (
    <a href="#" className="py-2 inline-block" onClick={handleLogout}>
      ログアウト
    </a>
  );
};

export default LogoutButton;
