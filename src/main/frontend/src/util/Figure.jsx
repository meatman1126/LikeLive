// import { Node, mergeAttributes } from "@tiptap/core";

// // figureノード
// export const Figure = Node.create({
//   name: "figure",

//   group: "block",

//   content: "image figcaption", // figureには画像とfigcaptionを含む

//   parseHTML() {
//     return [
//       {
//         tag: "figure",
//       },
//     ];
//   },

//   renderHTML({ HTMLAttributes }) {
//     return ["figure", mergeAttributes(HTMLAttributes), 0];
//   },
//   //   // キーバインディングでEnterキーの挙動をカスタマイズ
//   //   addKeyboardShortcuts() {
//   //     return {
//   //       Enter: ({ editor }) => {
//   //         // 次に新しい段落を挿入し、空のfigureを作らない
//   //         editor.commands.insertContent("<p></p>");
//   //         return true; // デフォルトのEnter挙動を無効化
//   //       },
//   //     };
//   //   },
// });

// // figcaptionノード
// export const Figcaption = Node.create({
//   name: "figcaption",

//   group: "block",

//   content: "inline*", // figcaptionにはインラインコンテンツを含む

//   parseHTML() {
//     return [
//       {
//         tag: "figcaption",
//       },
//     ];
//   },

//   renderHTML({ HTMLAttributes }) {
//     return ["figcaption", mergeAttributes(HTMLAttributes), 0];
//   },
//   // キーバインディングでEnterキーの挙動をカスタマイズ
//   //   addKeyboardShortcuts() {
//   //     return {
//   //       Enter: ({ editor }) => {
//   //         editor.commands.insertContent("<p></p>"); // 新しい段落を作成
//   //         return true; // Enterのデフォルト挙動を無効化
//   //       },
//   //     };
//   //   },
// });
