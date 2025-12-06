import { useGoogleLogin } from "@react-oauth/google";
import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import config from "../config/properties";
import fetchWithAuth from "../util/fetchUtil";
import { handleErrotToast } from "../util/toastUtil";

/**
 * 認証関連の処理を管理するカスタムフック
 * @param {boolean} isAuthenticated - 現在の認証状態
 * @param {function} setIsAuthenticated - 認証状態を更新する関数
 * @returns {Object} login関数とhandleAuthenticationCallback関数を含むオブジェクト
 */
export const useAuth = (isAuthenticated, setIsAuthenticated) => {
  const navigate = useNavigate();
  const processingAuth = useRef(false);

  // Google OAuth認証処理(Google認証画面にリダイレクト)
  const login = useGoogleLogin({
    onSuccess: async (tokenResponse) => {},
    flow: "auth-code",
    ux_mode: "redirect",
    redirect_uri: config.oauthRedirectUri,
    onError: (errorResponse) => {
      console.log(errorResponse);
      localStorage.setItem("ll_isAuthenticated", "false");
      localStorage.removeItem("ll_accessToken");
    },
  });

  // 認証コールバック処理
  const handleAuthenticationCallback = async (code) => {
    // 既に認証処理が実行中、認証コードがない、認証済みのいずれかの場合は何もしない
    if (processingAuth.current || !code || isAuthenticated) {
      return;
    }

    try {
      processingAuth.current = true;
      // 認証コードをAPIサーバーに送信
      const response = await fetch(`${config.apiBaseUrl}/api/oauth/callback`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ code }),
      });

      if (!response.ok) {
        throw new Error(response.status);
      }

      const data = await response.json();
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

        try {
          // 認証成功後、認証後処理を呼び出してユーザ情報を取得または登録
          const userResponse = await fetchWithAuth(
            `${config.apiBaseUrl}/api/login/after`,
            {
              method: "POST",
            }
          );
          const userData = await userResponse.json();
          localStorage.setItem("ll_userId", userData.id);

          // 初回ログイン判定
          const isFirstLogin = userData.updatedBy === "System";

          // 認証成功後、ダッシュボード画面に遷移
          navigate("/dashboard", {
            state: { isFirstLogin, userInfo: userData },
          });
        } catch (userError) {
          console.error("ユーザ情報取得エラー:", userError);
          handleAuthenticationError();
        }
      }
    } catch (error) {
      console.error("Error:", error);
      handleErrotToast("サインインに失敗しました。もう一度お試しください。");
      handleAuthenticationError();
    } finally {
      processingAuth.current = false;
    }
  };

  // 認証エラー時の共通処理
  const handleAuthenticationError = () => {
    // 認証を解除
    localStorage.setItem("ll_isAuthenticated", "false");
    // アクセストークンをlocalStorageから削除
    localStorage.removeItem("ll_accessToken");
    localStorage.removeItem("ll_refreshToken");
    localStorage.removeItem("ll_tokenExpires");
    localStorage.removeItem("ll_userId");

    setIsAuthenticated(false);
    navigate("/");
  };

  return {
    login,
    handleAuthenticationCallback,
  };
};
