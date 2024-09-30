"use client";
import React from "react";
import { appTitle, contactInfo } from "../config/properties";
export default function Footer() {
  return (
    <footer className="bg-black text-white py-6 border-t-2 border-white">
      <div className="container mx-auto px-4 flex flex-col md:flex-row justify-between items-start border-l-2 border-white">
        {/* タイトル */}
        <div className="mb-4 md:mb-0 flex-1">
          <h2 className="text-xl font-bold">{appTitle}</h2>
        </div>
        {/* コンタクト情報 */}
        <div className="flex-1 border-l-2 border-white pl-4">
          <h3 className="text-lg font-semibold mb-2">Contact</h3>
          <p>{contactInfo}</p>
        </div>
      </div>
    </footer>
  );
}
