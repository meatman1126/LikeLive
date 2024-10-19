// => Tiptap packages
import Bold from "@tiptap/extension-bold";
import Code from "@tiptap/extension-code";
import Document from "@tiptap/extension-document";
import Heading from "@tiptap/extension-heading";
import History from "@tiptap/extension-history";
import Image from "@tiptap/extension-image";
import Italic from "@tiptap/extension-italic";
import Paragraph from "@tiptap/extension-paragraph";
import Strike from "@tiptap/extension-strike";
import Text from "@tiptap/extension-text";
import Underline from "@tiptap/extension-underline";
import { EditorContent, useEditor } from "@tiptap/react";
// Custom
import React, { useEffect } from "react";
import "../style/tiptap.css";
/**
 *
 * @param {*} content ブログコンテンツ
 */
export default function TiptapViewer({ content }) {
  // Tiptapエディタの初期化
  const editor = useEditor({
    extensions: [
      Document,
      History,
      Paragraph,
      Text,
      Bold,
      Underline,
      Italic,
      Strike,
      Code,
      Image,
      Heading.configure({
        levels: [1, 2, 3],
      }),
    ],
    content: content,
    // 編集不可に設定
    editable: false,
  });

  // コンポーネントがアンマウントされた時にエディタをクリーンアップ
  useEffect(() => {
    return () => {
      if (editor) {
        editor.destroy();
      }
    };
  }, [editor]);

  return (
    <div className="editor">
      {/* Tiptapのエディタを表示 */}
      <EditorContent editor={editor} />
    </div>
  );
}
