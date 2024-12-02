import { useGoogleLogin } from "@react-oauth/google";
import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import "../App.css";
import Fotter from "../component/Fotter";
import Header from "../component/Header";
import config from "../config/properties";
import blogLarge from "../images/blog_large.jpeg";
import blogSmall from "../images/blog_small.jpeg";
import followLarge from "../images/follow_large.jpeg";
import followSmall from "../images/follow_small.jpeg";
import friend from "../images/friend.png";
import top from "../images/top.png";
import fetchWithAuth from "../util/fetchUtil";

export default function Top({ isAuthenticated, setIsAuthenticated }) {
  const [searchParams] = useSearchParams();
  const code = searchParams.get("code"); // 認証コードをURLから取得
  const navigate = useNavigate();
  let isFirstLogin = true;

  const [errorMessage, setErrorMessage] = useState(""); // エラーメッセージ用のstate

  useEffect(() => {
    // 認証コードがある場合、サーバーにリクエストを送信
    if (code && !isAuthenticated) {
      fetch(`${config.apiBaseUrl}/api/oauth/callback`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ code }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(response.status);
          }
          return response.json();
        })
        .then((data) => {
          // サーバーから返ってきたデータを使用して、ログイン状態を更新
          if (data.access_token) {
            setIsAuthenticated(true);
            // 認証が成功したら localStorage に保存し、状態を更新
            localStorage.setItem("ll_isAuthenticated", "true");
            // アクセストークンをlocalStorageに保存
            localStorage.setItem("ll_accessToken", data.access_token);
            localStorage.setItem("ll_refreshToken", data.refresh_token);
            localStorage.setItem(
              "ll_tokenExpires",
              Date.now() + data.expires_in * 1000
            );

            // 認証成功後、/login/after API を呼び出してユーザ情報を取得または登録
            fetchWithAuth(`${config.apiBaseUrl}/api/login/after`, {
              method: "POST",
            })
              .then((response) => {
                return response.json();
              })
              .then((data) => {
                localStorage.setItem("ll_userId", data.id);
                // 必要に応じてユーザ情報を保存したり、状態を更新する処理
                if (data.updatedBy !== "System") {
                  isFirstLogin = false;
                }
                // 認証成功後、ダッシュボード画面に遷移
                navigate("/dashboard", {
                  state: { isFirstLogin: isFirstLogin, userInfo: data },
                });
              })
              .catch((userError) => {
                // 認証に失敗した場合
                console.error("ユーザ情報取得エラー:", userError);
                // 認証を解除
                localStorage.setItem("ll_isAuthenticated", "false");
                // アクセストークンをlocalStorageから削除
                localStorage.removeItem("ll_accessToken");
                localStorage.removeItem("ll_refreshToken");
                localStorage.removeItem("ll_tokenExpires");
                localStorage.removeItem("ll_userId");

                setIsAuthenticated(false);
                navigate("/");
              });
          }
        })
        .catch((error) => {
          console.error("Error:", error.json);
          // 認証失敗時にエラーメッセージをstateにセット
          setErrorMessage("ログインに失敗しました。もう一度お試しください。");
        });
    }
  }, []);

  const login = useGoogleLogin({
    onSuccess: async (tokenResponse) => {},
    flow: "auth-code", // リダイレクト方式を指定
    ux_mode: "redirect",
    redirect_uri: config.oauthRedirectUri, // リダイレクト先を指定

    onError: (errorResponse) => {
      console.log(errorResponse);
      localStorage.setItem("ll_isAuthenticated", "false");
      localStorage.removeItem("ll_accessToken");
    },
  });

  return (
    <div className="Top">
      <div className="bg-black">
        {/* エラーメッセージがあれば表示 */}
        {errorMessage && (
          <div className="error-message text-red">{errorMessage}</div>
        )}
        {/* ヘッダー */}
        <div className="top-0 left-0 w-full z-10">
          <Header
            onLogin={login}
            isAuthenticated={isAuthenticated}
            setIsAuthenticated={setIsAuthenticated}
          />
        </div>
        {/* タイトルセクション */}
        <section
          className="relative h-screen bg-cover bg-center border-b border-b-white"
          style={{ backgroundImage: `url(${top})` }}
        >
          {/* 左上のタイトル要素 */}
          <div className="absolute top-5 left-5 text-white pt-24">
            <h1 className="text-6xl font-bold">LikeLive</h1>
            <h1 className="text-5xl font-bold">「好きなもの」を語る場所</h1>
            <h5 className="mt-4 text-3xl pt-10 before:content-['•'] before:mr-2">
              アプリの役割
            </h5>
            <p className="mt-1 text-2xl">
              好きなアーティストについて発信するためのプラットフォームです
            </p>
            <p className="mt-1 text-2xl">
              あなたのニッチな趣味が刺さる人に向けて
            </p>
          </div>

          {/* 中央下部に配置されたボタン要素 */}
          {!isAuthenticated && (
            <div className="absolute bottom-5 left-1/2 transform -translate-x-1/2">
              <button
                className="px-5 py-3 bg-teal-500 text-white rounded-full shadow-lg hover:bg-teal-600"
                onClick={() => login()}
              >
                Google連携で始める
              </button>
            </div>
          )}
        </section>

        {/* ブログセクション */}
        <section className="section-blog my-5 text-center border-b border-b-white">
          <h2 className="text-6xl font-bold mb-3 text-white text-left pl-2">
            ブログ
          </h2>
          {/* 画像を中央寄せ、左右に10%の余白、下に5%の余白 */}
          <div>
            <picture>
              {/* スマートフォン用画像 */}
              <source media="(max-width: 768px)" srcSet={blogSmall} />
              {/* デスクトップ用画像 */}
              <source media="(min-width: 769px)" srcSet={blogLarge} />

              {/* デフォルト画像（<img>タグのsrc属性） */}
              <img
                src={blogSmall}
                alt="ブログのイメージ"
                className="h-auto mx-auto w-4/5 mb-8"
              />
            </picture>
          </div>
        </section>
        {/* フォローセクション */}
        <section className="section-follow my-5 text-center border-b border-b-white">
          <h2 className="text-6xl font-bold mb-3 text-white text-left pl-2">
            フォロー
          </h2>
          <div>
            <picture>
              {/* スマートフォン用画像 */}
              <source media="(max-width: 768px)" srcSet={followSmall} />
              {/* デスクトップ用画像 */}
              <source media="(min-width: 769px)" srcSet={followLarge} />

              {/* デフォルト画像（<img>タグのsrc属性） */}
              <img
                src={followSmall}
                alt="フォローのイメージ"
                className="h-auto mx-auto w-4/5 mb-8"
              />
            </picture>
          </div>
        </section>

        {/* 友達コンテンツ */}
        <section className="main-content text-white py-10 border-b border-b-white">
          {/* 見出し（左寄せ） */}
          <h2 className="text-4xl mb-5 pl-2 text-left font-bold">
            自己満足で繋がる
          </h2>

          {/* 横に2等分のコンテンツ（画像左、説明右） */}
          <div className="flex flex-col md:flex-row items-center p-5">
            {/* 画像部分（左側） */}
            <div className="w-full md:w-1/2 mb-5 md:mb-0">
              <img
                src={friend}
                alt="友達コンテンツ"
                className="w-4/5 mx-auto"
              />
            </div>

            {/* 説明部分（右側） */}
            <div className="w-full md:w-1/2 text-left md:pl-8">
              <p className="text-lg mb-3">オタク会話が通じる楽しさ</p>
              <p className="text-lg">
                LikeLiveは、音楽好きのための専用プラットフォームです。自分が好きな音楽、アーティストについて自由に発言できる場所と共感できるユーザとの交流を提供します。
              </p>
            </div>
          </div>
        </section>
        {/* フッター */}
        <Fotter />
      </div>
    </div>
  );
}
