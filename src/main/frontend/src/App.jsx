import { GoogleOAuthProvider } from "@react-oauth/google";
import React, { useState } from "react";
import {
  Navigate,
  Route,
  BrowserRouter as Router,
  Routes,
} from "react-router-dom";
import "./App.css";
import ProtectedRoute from "./config/ProtectedRouter";
import Dashboard from "./page/Dashboard";
import Sample from "./page/Sample";
import Top from "./page/Top";
export default function App() {
  // 認証状態を管理するためのuseState
  const [isAuthenticated, setIsAuthenticated] = useState(
    localStorage.getItem("ll_isAuthenticated") === "true"
  );
  return (
    <div className="App">
      {/* GoogleOAuthProviderを追加して、clientIdを設定 */}
      <GoogleOAuthProvider clientId="213450167808-akmtr8dle4bg56tvlhcipjah4sqbjsp9.apps.googleusercontent.com">
        <Router>
          <Routes>
            {/* 認証が不要なページ */}
            <Route
              path="/"
              element={
                <Top
                  isAuthenticated={isAuthenticated}
                  setIsAuthenticated={setIsAuthenticated}
                />
              }
            />
            {/* GoogleOAuth認証成功後の挙動 */}
            <Route
              path="/login/callback"
              element={
                <Top
                  isAuthenticated={isAuthenticated}
                  setIsAuthenticated={setIsAuthenticated}
                />
              }
            />
            {/* 認証が必要なページ */}
            <Route
              path="/sample"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Sample />
                </ProtectedRoute>
              }
            />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Dashboard
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                  />
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </GoogleOAuthProvider>
    </div>
  );
}
