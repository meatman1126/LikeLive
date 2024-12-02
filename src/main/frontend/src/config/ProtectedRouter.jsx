import React from "react";
import { Navigate } from "react-router-dom";

/**
 * 認証状態に応じてアクセス可能なルートを返却します。
 *
 * @param {boolean} isAuthenticated ログイン状態を保持、ログイン済みの場合true
 * @returns 未認証の場合ルートコンポーネントへリダイレクト、認証済みの場合子要素
 */
function ProtectedRoute({ isAuthenticated, children }) {
  return isAuthenticated ? children : <Navigate to="/" />;
}

export default ProtectedRoute;
