// LoadingContext.js
import React, { createContext, useContext, useState } from "react";

// コンテキストを作成
const LoadingContext = createContext();

// LoadingProvider コンポーネントを作成して、子コンポーネントにローディング状態を提供
export const LoadingProvider = ({ children }) => {
  const [isLoading, setIsLoading] = useState(false);

  // ローディングの開始と停止メソッドを定義
  const startLoading = () => setIsLoading(true);
  const stopLoading = () => setIsLoading(false);

  return (
    <LoadingContext.Provider value={{ isLoading, startLoading, stopLoading }}>
      {children}
    </LoadingContext.Provider>
  );
};

// ローディング状態を利用するためのフック
export const useLoading = () => {
  return useContext(LoadingContext);
};
