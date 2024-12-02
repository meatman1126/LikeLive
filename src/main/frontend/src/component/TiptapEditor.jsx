import classNames from "classnames";
import React, {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from "react";
import config from "../config/properties";
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
import { BubbleMenu, EditorContent, useEditor } from "@tiptap/react";
// Custom
import * as Icons from "../images/Icons";
import "../style/tiptap.css";
import fetchWithAuth from "../util/fetchUtil";

/**
 * Tiptapエディタでブログコンテンツを編集するコンポーネント
 *
 * 親コンポーネントでeditorにアクセスできるようにforwardRefを使用
 * 編集対象のブログがある場合propsにcontentが指定されて呼び出される
 */
const TiptapEditor = forwardRef((props, ref) => {
  // Tiptapエディタ
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
    // コンテンツが指定された場合それを読み込む、未指定の場合は空
    content: "",
    onUpdate: ({ editor }) => {
      // カーソルが移動したらボタンの位置を更新
      updateInsertButtonPosition(editor);
    },
  });
  // 親コンポーネントからeditorにアクセスできるようにする
  useImperativeHandle(ref, () => editor, [editor]);

  // props.contentが変更された場合にエディタのコンテンツを更新する
  useEffect(() => {
    if (editor && props.content) {
      editor.commands.setContent(props.content);
    }
  }, [props.content, editor]);

  // 挿入メニューのボタン（onClickとclassNameをプロパティに含める）
  const insertButtons = [
    {
      label: "大見出し",
      onClick: () => editor.chain().focus().toggleHeading({ level: 1 }).run(),
      icon: <Icons.H1Icon />,
    },
    {
      label: "中見出し",
      onClick: () => editor.chain().focus().toggleHeading({ level: 2 }).run(),
      icon: <Icons.H2Icon />,
    },
    {
      label: "小見出し",
      onClick: () => editor.chain().focus().toggleHeading({ level: 3 }).run(),
      icon: <Icons.H3Icon />,
    },
    {
      label: "画像",
      onClick: () => {
        fileInputRef.current.click();
      },
      icon: <Icons.ImageIcon />,
    },
    // 将来的に追加されるボタンもこの配列に追加すればOK
  ];

  // 挿入ボタンのメニュー表示有無
  const [openInsertMenu, setOpenInsertMenu] = useState(false);
  // 挿入ボタンの位置
  const [insertButtonPosition, setInsertButtonPosition] = useState({
    top: 0,
    left: 0,
  });
  // 挿入ボタンの位置をカーソルに合わせて移動する処理
  const updateInsertButtonPosition = (editor) => {
    const { from } = editor.state.selection;

    // エディタ全体の位置を取得
    const editorRect = editor.view.dom.getBoundingClientRect();

    // カーソル位置を画面全体基準で取得
    const coords = editor.view.coordsAtPos(from);

    // エディタ要素に対する相対的な位置を計算
    const relativeTop = coords.top - editorRect.top;

    // ボタンをカーソルがある行の左端に配置(微調整は固定値で実施)
    setInsertButtonPosition({ top: relativeTop - 10, left: -20 });
  };
  // エディタに合わせて挿入ボタンの位置を調整する
  useEffect(() => {
    if (editor) {
      // エディタのフォーカス/カーソルが変わったら位置を更新
      updateInsertButtonPosition(editor);
      editor.on("selectionUpdate", () => updateInsertButtonPosition(editor));
    }
  }, [editor]);

  // ファイル選択用のref
  const fileInputRef = useRef(null);

  // ファイル選択時の処理
  const handleImageInsert = async (event) => {
    const file = event.target.files[0];
    if (file) {
      // ファイルアップロードAPIを呼び出す
      await callUploadAPI(file);
      // 選択されたファイル内容をクリア
      fileInputRef.current.value = "";
    }
  };

  // 写真アップロード用のAPI呼び出し処理
  const callUploadAPI = async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetchWithAuth(
        `${config.apiBaseUrl}/api/file/save`,
        {
          method: "POST",
          body: formData,
        }
      );
      const imageUrl = await response.text();
      const src = `${config.apiBaseUrl}/api/public/files/${imageUrl}`;

      // Tiptapエディタに画像のパスを挿入
      editor.chain().focus().setImage({ src: src }).run();
    } catch (error) {
      console.error("Image upload failed:", error);
    }
  };
  // Tiptapエディタの処理メソッド
  const toggleBold = useCallback(() => {
    editor.chain().focus().toggleBold().run();
  }, [editor]);

  const toggleUnderline = useCallback(() => {
    editor.chain().focus().toggleUnderline().run();
  }, [editor]);

  const toggleItalic = useCallback(() => {
    editor.chain().focus().toggleItalic().run();
  }, [editor]);

  const toggleStrike = useCallback(() => {
    editor.chain().focus().toggleStrike().run();
  }, [editor]);

  const toggleCode = useCallback(() => {
    editor.chain().focus().toggleCode().run();
  }, [editor]);
  // Tiptapエディタの処理メソッド終了

  if (!editor) {
    return null;
  }

  return (
    <div className="editor editor-mini">
      {/* </button> */}
      <div
        className="absolute z-10"
        style={{
          top: insertButtonPosition.top + "px",
          left: insertButtonPosition.left + "px",
        }}
      >
        <button
          className="w-10 h-10 menu-button mr-4 rounded-full border border-solid border-gray-300 p-2"
          onClick={() => setOpenInsertMenu(!openInsertMenu)}
        >
          {openInsertMenu ? "×" : "+"} {/* 挿入ボタン、展開時は×を表示 */}
        </button>
        {/* 挿入ボタン押下時に表示されるリスト形式のメニュー */}
        {openInsertMenu && (
          <div className="insert-menu bg-white p-2 shadow-lg border border-gray-200">
            <ul className="space-y-2">
              {" "}
              {/* 各liの間にスペースを追加するために space-y-2 を適用 */}
              {insertButtons.map((button, index) => (
                <li key={index} className="list-none">
                  {" "}
                  {/* list-none でデフォルトのリストスタイルを無効化 */}
                  <button
                    onClick={() => {
                      button.onClick();
                      setOpenInsertMenu(false); // ボタン押下後にメニューを閉じる
                    }}
                    className="flex items-center space-x-2" // 横並びにするためのスタイルを追加
                  >
                    {button.icon} {/* アイコンを表示 */}
                    <span>{button.label}</span> {/* ラベルを表示 */}{" "}
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}
        {/* 非表示のファイル入力 */}
        <input
          type="file"
          accept="image/*"
          ref={fileInputRef}
          onChange={(e) => {
            handleImageInsert(e);
          }}
          style={{ display: "none" }}
        />
      </div>

      {/* コンテンツの編集メニュー */}
      <BubbleMenu
        pluginKey="bubbleMenuText"
        className="bubble-menu-dark"
        tippyOptions={{ duration: 150 }}
        editor={editor}
        shouldShow={({ editor, state }) => {
          // 現在選択されているノードを取得
          const { from, to } = state.selection;
          const node = state.doc.nodeAt(from);

          // 画像ノードの場合、バブルメニューを非表示にする
          if (node && node.type.name === "image") {
            return false;
          }

          // 範囲が選択されているときにのみバブルメニューを表示
          return from !== to;
        }}
      >
        <button
          className="menu-button"
          onClick={() => editor.chain().focus().undo().run()}
          disabled={!editor.can().undo()}
        >
          <Icons.RotateLeft />
        </button>
        <button
          className="menu-button"
          onClick={() => editor.chain().focus().redo().run()}
          disabled={!editor.can().redo()}
        >
          <Icons.RotateRight />
        </button>
        <button
          className={classNames("menu-button", {
            "is-active": editor.isActive("bold"),
          })}
          onClick={toggleBold}
        >
          <Icons.Bold />
        </button>
        <button
          className={classNames("menu-button", {
            "is-active": editor.isActive("underline"),
          })}
          onClick={toggleUnderline}
        >
          <Icons.Underline />
        </button>
        <button
          className={classNames("menu-button", {
            "is-active": editor.isActive("intalic"),
          })}
          onClick={toggleItalic}
        >
          <Icons.Italic />
        </button>
        <button
          className={classNames("menu-button", {
            "is-active": editor.isActive("strike"),
          })}
          onClick={toggleStrike}
        >
          <Icons.Strikethrough />
        </button>
        <button
          className={classNames("menu-button", {
            "is-active": editor.isActive("code"),
          })}
          onClick={toggleCode}
        >
          <Icons.Code />
        </button>
      </BubbleMenu>
      <div>
        <EditorContent editor={editor} />
      </div>
    </div>
  );
});
export default TiptapEditor;
