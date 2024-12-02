import React from "react";

export default function ConfirmModal({ message, onConfirm, onCancel }) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-gray-800 bg-opacity-50 z-50">
      <div className="bg-white p-6 rounded shadow-lg sm:w-1/3 w-96">
        <p className="mb-4 text-lg text-left">{message}</p>{" "}
        {/* メッセージを中央寄せ */}
        <div className="flex justify-center space-x-4">
          {" "}
          {/* ボタンを中央寄せ、スペースを追加 */}
          <button
            className="bg-gray-300 text-black py-2 px-6 rounded w-32"
            onClick={onCancel}
          >
            キャンセル
          </button>
          <button
            className="bg-blue-500 text-white py-2 px-6 rounded w-32"
            onClick={onConfirm}
          >
            OK
          </button>
        </div>
      </div>
    </div>
  );
}
