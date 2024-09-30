// "use client";
// import "@fortawesome/fontawesome-free/css/all.min.css";
// import React from "react";

// function HumbergerMenu() {
//   const [isOpen, setIsOpen] = React.useState(false);

//   const toggleMenu = () => {
//     setIsOpen(!isOpen);
//   };

//   return (
//     <div className="relative">
//       <button
//         className="flex items-center w-10 px-3 py-3 border rounded text-black bg-white"
//         onClick={toggleMenu}
//       >
//         <i className="fas fa-bars"></i>
//       </button>
//       <div
//         className={`absolute top-full mt-2 w-40 bg-white text-black rounded transition-transform duration-300 ${
//           isOpen
//             ? "transform translate-y-0 opacity-100"
//             : "transform -translate-y-10 opacity-0"
//         }`}
//         style={{ display: isOpen ? "block" : "none" }}
//       >
//         <ul className="flex flex-col">
//           <li className="px-4 py-2 hover:bg-gray-700">Menu Item 1</li>
//           <li className="px-4 py-2 hover:bg-gray-700">Menu Item 2</li>
//           <li className="px-4 py-2 hover:bg-gray-700">Menu Item 3</li>
//         </ul>
//       </div>
//     </div>
//   );
// }

// export default HumbergerMenu;

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
