import React from "react";

// Editorの状態をJSONとして表示するコンポーネント
export default function EditorPreview({ editor }) {
  console.log("Editor instance:", editor); // これでエディタの初期化状況を確認

  if (!editor) {
    return null; // editorが存在しない場合は何も表示しない
  }

  return (
    <div>
      <h2>Editorの状態 (JSON形式)</h2>
      <pre>{JSON.stringify(editor.getJSON(), null, 2)}</pre>
    </div>
  );
}
