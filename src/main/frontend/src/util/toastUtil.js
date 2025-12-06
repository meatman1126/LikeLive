import { toast } from "react-toastify";

// 保存処理が成功した場合のトースト通知
export const handleSuccessToast = (message) => {
  toast.success(message, {
    position: "top-right", // 位置を指定
    autoClose: 1000, // 自動で閉じるまでの時間（ミリ秒）
    closeOnClick: true, // クリックで閉じる
  });
};

// エラー時のトースト通知
export const handleErrotToast = (message) => {
  toast.error(message, {
    position: "top-right", // 位置を指定
    autoClose: 2000, // 自動で閉じるまでの時間（ミリ秒）
    closeOnClick: true, // クリックで閉じる
  });
};
