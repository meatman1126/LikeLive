// このコンポーネントは不要です。
"use client";
import "@fortawesome/fontawesome-free/css/all.min.css";
import React from "react";

export default function HumbergerMenu({ toggleMenu, isMenuOpen }) {
  return (
    <div className="relative">
      {/* ボタン部分 */}
      {/* <button
        className="flex items-center w-10 px-3 py-3 border rounded text-black bg-white"
        onClick={toggleMenu}
      >
        {/* ハンバーガーアイコンに回転効果を追加 */}
      {/* </button> */}
      {/* humbergerbutton */}
      <button onClick={toggleMenu} type="button" className="z-10 space-y-2">
        <div
          className={
            isMenuOpen
              ? "w-8 h-0.5 bg-gray-600 translate-y-2.5 rotate-45 transition duration-500 ease-in-out"
              : "w-8 h-0.5 bg-gray-600 transition duration-500 ease-in-out"
          }
        />
        <div
          className={
            isMenuOpen
              ? "opacity-0 transition duration-500 ease-in-out"
              : "w-8 h-0.5 bg-gray-600 transition duration-500 ease-in-out"
          }
        />
        <div
          className={
            isMenuOpen
              ? "w-8 h-0.5 bg-gray-600 -rotate-45 transition duration-500 ease-in-out"
              : "w-8 h-0.5 bg-gray-600 transition duration-500 ease-in-out"
          }
        />
      </button>
    </div>
  );
}

// export default HumbergerMenu;
