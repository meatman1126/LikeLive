import { GoogleOAuthProvider } from "@react-oauth/google";
import React, { useState } from "react";
import {
  Navigate,
  Route,
  BrowserRouter as Router,
  Routes,
} from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./App.css";

import Spinner from "./component/Spinner";

import StoryComponent from "./component/CommentList";
import ProtectedRoute from "./config/ProtectedRouter";
import Blog from "./page/Blog";
import BlogSearch from "./page/BlogSearch";
import Dashboard from "./page/Dashboard";
import Top from "./page/Top";
import User from "./page/User";
import { LoadingProvider, useLoading } from "./util/LoadingContext";

function AppContent() {
  // 認証状態を管理するためのuseState
  const [isAuthenticated, setIsAuthenticated] = useState(
    localStorage.getItem("ll_isAuthenticated") === "true"
  );
  const { isLoading } = useLoading(); // ローディング状態を取得

  return (
    <div className="App">
      {/* isLoading が true の場合はスピナーを表示 */}
      {isLoading && <Spinner />}
      {/* アプリ全体でトーストを使用するためにToastContainerを追加 */}
      <ToastContainer />
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
            {/* 以降は認証が必要なページ */}
            {/* ブログ編集画面 */}
            <Route
              path="/blog/edit/:targetBlogId"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Blog
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                    isEdit={true}
                  />
                </ProtectedRoute>
              }
            />
            {/* ブログ新規作成画面 */}
            <Route
              path="/blog/create"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Blog
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                    isEdit={true}
                    key={window.location.pathname}
                  />
                </ProtectedRoute>
              }
            />
            {/* ブログ閲覧画面 */}
            <Route
              path="/blog/:targetBlogId"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Blog
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                    isEdit={false}
                  />
                </ProtectedRoute>
              }
            />

            <Route
              path="/blog/search"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <BlogSearch
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                  />
                </ProtectedRoute>
              }
            />
            <Route
              path="/sample"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <StoryComponent />
                </ProtectedRoute>
              }
            />
            {/* ダッシュボード画面 */}
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
            {/* ユーザプロフィール画面 */}
            <Route
              path="/user/profile"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <User
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                  />
                </ProtectedRoute>
              }
            />
            {/* 他ユーザのプロフィール画面 */}
            <Route
              path="/user/:targetUserId"
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <User
                    isAuthenticated={isAuthenticated}
                    setIsAuthenticated={setIsAuthenticated}
                  />
                </ProtectedRoute>
              }
            />
            {/* 想定していないパスへのリクエストはトップ画面へリダイレクトする */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </GoogleOAuthProvider>
    </div>
  );
}

export default function App() {
  return (
    <LoadingProvider>
      <AppContent />
      {/* useLoadingはLoadingProvider内で呼び出されるようになります */}
    </LoadingProvider>
  );
}
