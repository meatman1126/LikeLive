import { toast } from "react-toastify";

// 保存処理が成功した場合のトースト通知
export const handleSuccessToast = () => {
  toast.success("ブログが正常に保存されました！", {
    position: "top-right", // 位置を指定
    autoClose: 2000, // 自動で閉じるまでの時間（ミリ秒）
    closeOnClick: true, // クリックで閉じる
  });
};
