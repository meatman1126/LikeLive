import React, { useCallback, useEffect, useRef, useState } from "react";
import Cropper from "react-easy-crop";
import { useNavigate } from "react-router-dom";
import config from "../config/properties";
import { blogCategories } from "../constants/enum";
import { getCroppedImg } from "../util/cropImageToCanvas";
import { getCodeByDescription } from "../util/enumUtil";
import fetchWithAuth from "../util/fetchUtil";
import { useLoading } from "../util/LoadingContext";
import { handleSuccessToast } from "../util/toastUtil";
import ConfirmModal from "./ConfirmModal";
import TiptapEditor from "./TiptapEditor";

/**
 *
 * @param {*} targetBlogId  編集対象のブログID
 * @returns ブログ編集画面
 */
export default function BlogEditor({ targetBlogId }) {
  // セットリストの最大曲数
  const MAX_SETLIST_SONGS = 50;
  // アンコールセクションの最大曲数
  const MAX_ENCORE_SONGS = 10;
  // アンコールセクションの最大曲数
  const MAX_ENCORE_SECTIONS = 5;

  // ローディング制御メソッドを取得
  const { startLoading, stopLoading } = useLoading();
  // useNavigateフック
  const navigate = useNavigate();
  // Tiptapエディタref
  const editorRef = useRef(null);

  // サムネイル画像管理に使用するstate
  //　DBから取得したサムネイル画像を管理
  const [thumbnailUrl, setThumbnailUrl] = useState(null);

  // 選択されたプロフィール画像を管理するstate
  const [selectedImage, setSelectedImage] = useState(null);

  // 切り取られたプロフィール画像を管理するstate
  const [croppedImage, setCroppedImage] = useState(null);

  // 画像切り取りに使用する変数
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);

  // 確認モーダル表示状態
  const [isModalOpen, setIsModalOpen] = useState(false);
  // ブログタイトル
  const [title, setTitle] = useState("");
  // タイトル入力エラー状態
  const [titleError, setTitleError] = useState(false);
  // ブログカテゴリー
  const [category, setCategory] = useState(blogCategories[0].code);
  // ブログコンテント
  const [content, setContent] = useState(null);
  // セットリスト表示有無
  const [showSetlist, setShowSetlist] = useState(false);
  // セットリスト情報（本編）
  const [setlist, setSetlist] = useState([]);
  // セットリストのエラー情報（本編）
  const [setlistErrors, setSetlistErrors] = useState([]);
  // セットリスト情報（アンコールセクション）
  const [encoreSections, setEncoreSections] = useState([]);
  // セットリストのエラー情報（アンコールセクション）
  const [encoreSectionErrors, setEncoreSectionErrors] = useState([]);
  // ブログステータス
  const [blogStatus, setBlogStatus] = useState(null);

  // 関連アーティストの管理
  // 関連アーティストリスト
  const [relatedArtists, setRelatedArtists] = useState([]);
  // 選択可能なアーティストリスト
  const [availableArtistOptions, setAvailableArtistOptions] = useState([]);
  // プルダウン表示状態
  const [showArtistDropdown, setShowArtistDropdown] = useState(false);

  // エラーメッセージ用のstate
  const [error, setError] = useState(null);

  // 画面描画時の処理
  useEffect(() => {
    // ログインユーザの好きなアーティストを取得します。
    const fetchArtists = async () => {
      try {
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/user/my-artists`
        );
        if (response.ok) {
          const data = await response.json();
          if (data) {
            // 取得したアーティストから関連アーティストに追加済みのものを除く
            const filteredArtists = data.filter(
              (artist) =>
                !relatedArtists.some(
                  (relatedArtist) => relatedArtist.id === artist.id
                )
            );
            // フィルタリング後のアーティストデータをセット
            setAvailableArtistOptions(filteredArtists);
          }
        } else {
          console.error("アーティストリストの取得に失敗しました。");
          throw new Error("データの取得に失敗しました");
        }
      } catch (error) {
        console.error("エラーが発生しました:", error);
      }
    };

    // 指定されたブログデータ取得処理
    const fetchBlogInfo = async (blogId) => {
      try {
        const response = await fetchWithAuth(
          `${config.apiBaseUrl}/api/blog/${blogId}`
        );
        if (response.ok) {
          const data = await response.json();

          // ブログデータの各要素をセット
          setTitle(data.blog.title);
          setThumbnailUrl(data.blog.thumbnailUrl);
          setSetlist(data.blog.setlist.mainSetList);
          setEncoreSections(data.blog.setlist.encoreSections);
          setCategory(getCodeByDescription(blogCategories, data.blog.category));
          setRelatedArtists(data.artistList);
          setContent(data.blog.content);
          setBlogStatus(data.blog.status);

          // エラー配列の初期化処理
          const setlistErrorsArray = Array(
            data.blog.setlist.mainSetList.length
          ).fill(false);
          setSetlistErrors(setlistErrorsArray);
          const encoreSectionErrorsArray = data.blog.setlist.encoreSections.map(
            (section) => Array(section.length).fill(false)
          );
          setEncoreSectionErrors(encoreSectionErrorsArray);

          // セットリスト表示状態の判定
          // 本編またはアンコールセクションが追加済みの場合表示
          if (
            data.blog.setlist.mainSetList.length > 0 ||
            data.blog.setlist.encoreSections.length > 0
          ) {
            setShowSetlist(true);
          }
        } else {
          console.error("ブログ情報の取得に失敗しました。");
        }
      } catch (error) {
        console.error("エラーが発生しました:", error);
        setError("対象のブログデータが見つかりませんでした。");
      }
    };

    try {
      startLoading();
      // 編集対象のブログIDが指定されている場合ブログデータを取得
      if (targetBlogId) {
        fetchBlogInfo(targetBlogId);
      }
      // ログインユーザの好きなアーティストデータを取得
      fetchArtists();
    } catch (error) {
      console.error("エラーが発生しました:", error);
    } finally {
      stopLoading();
    }
  }, [targetBlogId]);

  // ファイル選択時の処理
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      setSelectedImage(reader.result);
    };
  };

  const onCropComplete = useCallback((croppedArea, croppedAreaPixels) => {
    setCroppedAreaPixels(croppedAreaPixels);
  }, []);

  const fileInputRef = useRef(null);

  const showCroppedImage = useCallback(async () => {
    try {
      const croppedImage = await getCroppedImg(
        selectedImage,
        croppedAreaPixels
      );
      setCroppedImage(croppedImage);
      setSelectedImage(null);
      // input要素のファイル選択をリセット
      if (fileInputRef.current) {
        fileInputRef.current.value = ""; // これでinputの選択もリセットされる
      }
    } catch (e) {
      console.error(e);
    }
  }, [selectedImage, croppedAreaPixels]);

  // プロフィール画像またはアイコンをクリックして画像選択を開始
  const triggerImageSelect = () => {
    document.getElementById("profileImageInput").click();
  };

  // 関連アーティストの変更をトリガーに発火する処理
  useEffect(() => {
    const updateAvailableArtists = () => {
      const filteredArtists = availableArtistOptions.filter(
        (artist) => !relatedArtists.some((related) => related.id === artist.id)
      );
      setAvailableArtistOptions(filteredArtists);
    };
    updateAvailableArtists();
  }, [relatedArtists]);

  // 関連アーティスト追加処理
  const handleAddArtist = (artistId) => {
    const artist = availableArtistOptions.find((a) => a.id === artistId);
    if (artist) {
      // 選択したアーティストが既に関連アーティストに追加されている場合スキップ
      if (!relatedArtists.some((a) => a.id === artistId)) {
        setRelatedArtists([...relatedArtists, artist]);
      }
      // 追加されたアーティストを選択肢から除外
      setAvailableArtistOptions(
        availableArtistOptions.filter((a) => a.id !== artistId)
      );
    }
  };

  // 関連アーティスト削除処理
  const handleRemoveArtist = (artistId) => {
    const artist = relatedArtists.find((a) => a.id === artistId);
    if (artist) {
      setRelatedArtists(relatedArtists.filter((a) => a.id !== artistId));
      // 削除したアーティストを選択肢に戻す（選択肢に同一のアーティストがいる場合スキップ）
      if (!availableArtistOptions.some((a) => a.id === artistId)) {
        setAvailableArtistOptions([...availableArtistOptions, artist]);
      }
    }
  };

  // 「閉じる」ボタン押下時の処理
  const handleCloseClick = () => {
    // 確認モーダルを開く
    setIsModalOpen(true);
  };

  // 「OK」ボタン押下時
  const handleConfirm = () => {
    // 確認モーダルを閉じ、ダッシュボード画面に遷移
    setIsModalOpen(false);
    navigate("/dashboard");
  };

  // 「キャンセル」ボタン押下時
  const handleCancel = () => {
    // 確認モーダルを閉じる
    setIsModalOpen(false);
  };

  // タイトル入力時の処理
  const handleTitleChange = (e) => {
    setTitle(e.target.value);
    // タイトル入力エラーがある場合はエラーを解除する
    if (e.target.value.trim()) {
      setTitleError(false);
    }
  };

  // カテゴリ変更時の処理
  const handleCategoryChange = (e) => {
    setCategory(Number(e.target.value));
  };

  /**
   * 「曲数を追加」ボタン押下時の処理
   * @param {*} isEncore アンコールパートの曲数追加時の場合true
   * @param {*} encoreIndex アンコールパートのセクション番号
   */
  const addMoreTracks = (isEncore = false, encoreIndex = null) => {
    // アンコールパートの曲数追加の場合
    if (isEncore && encoreIndex !== null) {
      // 追加対象のセクションの曲数が上限を超えている場合追加処理はスキップ
      if (encoreSections[encoreIndex].length < MAX_ENCORE_SONGS) {
        // アンコールセクションの既存トラック数から次のtrackNumberを決定
        const nextEncoreTrackNumber = encoreSections[encoreIndex].length + 1;
        // 2曲分のトラックを追加
        const newEncoreTracks = [...Array(2)].map((_, index) => ({
          trackNumber: nextEncoreTrackNumber + index,
          trackName: "",
        }));

        setEncoreSections((prevEncoreSections) => {
          const updatedEncoreSections = [...prevEncoreSections];
          updatedEncoreSections[encoreIndex] = [
            ...updatedEncoreSections[encoreIndex],
            ...newEncoreTracks,
          ];
          return updatedEncoreSections;
        });
      }
    } else {
      // 本編の曲数追加の場合
      // トラック数が上限を超える場合追加処理はスキップ
      if (setlist.length < MAX_SETLIST_SONGS) {
        // 本編セットリストの既存トラック数から次のtrackNumberを決定
        const nextTrackNumber = setlist.length + 1;

        // 5曲分のトラックを追加
        const newTracks = [...Array(5)].map((_, index) => ({
          trackNumber: nextTrackNumber + index,
          trackName: "",
        }));

        setSetlist([...setlist, ...newTracks]);
      }
    }
  };

  // 「アンコール枠を追加」ボタン押下時の処理
  const addEncoreSection = () => {
    // アンコールセクションを追加する、初期トラックを2曲分追加
    const newEncoreSection = [...Array(2)].map((_, index) => ({
      trackNumber: index + 1,
      trackName: "",
    }));

    setEncoreSections([...encoreSections, newEncoreSection]);

    // エラー配列にも同じ数のエントリを追加;
    setEncoreSectionErrors([...encoreSectionErrors, Array(2).fill(false)]);
  };

  // 「アンコール枠を削除」ボタン押下時の処理
  const removeEncoreSection = () => {
    // アンコールパートが未追加の場合は処理をスキップ
    if (encoreSections.length > 0) {
      // 最後のセクションを取り除く
      setEncoreSections(encoreSections.slice(0, -1));
    }
  };

  /**
   * 曲名入力時の処理
   * @param {*} index トラックナンバー（何曲目の曲名か）
   * @param {*} value 入力値
   * @param {*} isEncore アンコールパートを編集した場合true
   * @param {*} encoreIndex アンコールパートのセクションナンバー
   */
  const handleTrackChange = (
    index,
    value,
    isEncore = false,
    encoreIndex = null
  ) => {
    // アンコールパート編集時
    if (isEncore && encoreIndex !== null) {
      const updatedEncoreSections = [...encoreSections];
      updatedEncoreSections[encoreIndex][index].trackName = value;
      setEncoreSections(updatedEncoreSections);

      // 入力エラーを解除する
      if (value) {
        const updatedEncoreSectionErrors = [...encoreSectionErrors];
        updatedEncoreSectionErrors[encoreIndex][index] = false;
        setEncoreSectionErrors(updatedEncoreSectionErrors);
      }
    } else {
      // 本編編集時

      const updatedSetlist = [...setlist];
      updatedSetlist[index].trackName = value;
      setSetlist(updatedSetlist);

      // 入力エラーを解除する
      if (value) {
        const updatedSetlistErrors = [...setlistErrors];
        updatedSetlistErrors[index] = false;
        setSetlistErrors(updatedSetlistErrors);
      }
    }
  };

  // セットリストの入力チェック処理
  const validateSetlist = () => {
    const newSetlistErrors = Array(setlist.length).fill(false);
    const newEncoreSectionErrors = encoreSections.map((section) =>
      Array(section.length).fill(false)
    );

    // 本編のチェック
    let isMainSetlistValid = true;
    setlist.forEach((track, index, array) => {
      //穴あき入力がある場合エラー
      if (index > 0 && track.trackName && !array[index - 1].trackName) {
        newSetlistErrors[index - 1] = true;
        isMainSetlistValid = false;
      }
    });

    // アンコールパートの入力有効フラグ
    let isEncoreValid = true;
    // アンコールパートのチェック処理（未入力チェックおよび穴あき入力チェック）

    // セクション毎にチェック
    encoreSections.forEach((section, sectionIndex) => {
      // 未入力セクションチェック
      const isSectionEmpty = section.every((track) => !track.trackName);
      if (isSectionEmpty) {
        // 全ての曲名が未入力の場合セクション全体をエラーとする
        newEncoreSectionErrors[sectionIndex] = Array(section.length).fill(true);
        isEncoreValid = false;
      }

      // 穴あきエラーチェック
      section.forEach((track, index, array) => {
        if (index > 0 && track.trackName && !array[index - 1].trackName) {
          newEncoreSectionErrors[sectionIndex][index - 1] = true;
          isEncoreValid = false;
        }
      });
    });

    setSetlistErrors(newSetlistErrors); // エラー状態を更新
    setEncoreSectionErrors(newEncoreSectionErrors); // エラー状態を更新

    return isMainSetlistValid && isEncoreValid;
  };

  // セットリストをAPI呼び出し用のJSON構造に変換します。
  const createSetlistJSON = () => {
    // 本編について曲名が未入力のものを除く
    const setlistInput = setlist
      .filter((track) => track.trackName.trim() !== "")
      .map((track, index) => ({
        ...track,
        trackNumber: index + 1,
      }));

    // アンコールパートについて曲名が未入力のものを除く
    const encoreSectionsInput = encoreSections
      .map((section) =>
        section
          .filter((track) => track.trackName.trim() !== "")
          .map((track, index) => ({
            ...track,
            trackNumber: index + 1,
          }))
      )
      // 空のセクションは除く
      .filter((section) => section.length > 0);

    return {
      mainSetList: setlistInput,
      encoreSections: encoreSectionsInput,
    };
  };

  /**
   * ブログデータ保存APIを呼び出します。
   * @param {*} isDraft 下書き有無（ブログステータスが下書きの場合true）
   */
  const callSaveAPI = async (isDraft = false) => {
    const editor = editorRef.current;
    if (editor) {
      const contentJSON = editor.getJSON();
      console.log("保存するデータ: ", contentJSON);
      console.log(contentJSON);
    } else {
      console.log("contentの読み込みに失敗しました。");
      return;
    }

    // FormDataを作成して、ブログデータを追加
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", JSON.stringify(editor.getJSON()));
    formData.append("status", isDraft ? "DRAFT" : "PUBLISHED");
    formData.append(
      "category",
      blogCategories.find((list) => list.code === category).description
    );
    formData.append("setlist", JSON.stringify(createSetlistJSON()));
    formData.append(
      "artistIdList",
      relatedArtists.map((artist) => artist.id)
    );
    // if (targetBlogId) {
    //   formData.append("id", targetBlogId);
    // }
    // croppedImageが設定されている場合はFormDataに追加
    if (croppedImage) {
      const response = await fetch(croppedImage);
      const blob = await response.blob(); // 画像データをBlob形式に変換
      formData.append("thumbnailImage", blob, "thumbnail.jpg"); // Blobデータとして追加
    }

    try {
      // targetBlogIdが指定されている場合は更新用のAPIを呼び出す。
      const url = targetBlogId
        ? `${config.apiBaseUrl}/api/blog/update/${targetBlogId}`
        : `${config.apiBaseUrl}/api/blog/create`;
      const response = await fetchWithAuth(url, {
        method: "POST", // POSTメソッドを使用
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        console.log("ブログが正常に保存されました: ", data);
        // 保存成功後閲覧画面に遷移する
        handleSuccessToast("ブログが保存されました");
        navigate(`/blog/${data.id}`);
      } else {
        console.error("ブログ保存に失敗しました");
      }
    } catch (error) {
      console.error("エラーが発生しました: ", error);
    }
  };

  // 「下書き保存」ボタン押下時の処理
  const handleDraftSave = async () => {
    // 入力内容チェック
    const isValid = validateSetlist();
    if (!isValid) {
      console.log("入力内容が不正です。");
      return;
    }
    console.log("下書き保存のためAPIを呼び出します");
    callSaveAPI(true);
  };

  // 「公開」「更新」ボタン押下時の処理
  const handleSave = () => {
    let hasErrors = false;
    // 入力チェックを行う
    // タイトル入力チェック
    if (!title.trim()) {
      setTitleError(true);
      hasErrors = true;
    } else {
      setTitleError(false);
    }

    // セットリストの入力チェック
    const isSetlistValid = validateSetlist();
    if (!isSetlistValid) {
      hasErrors = true;
    }

    if (hasErrors) {
      console.log("入力内容が不正です。");
      return;
    }

    console.log("保存処理のためのAPIを呼び出します。");
    callSaveAPI();
  };
  // エラーが発生した場合の処理
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-screen">
        <p className="text-red-500 mb-4">{error}</p>
        <button
          className="p-4 w-18 bg-gray-300 rounded-lg"
          onClick={() => navigate("/dashboard")}
        >
          ホームへ戻る
        </button>
      </div>
    );
  }

  return (
    <>
      {/* ボタン群 */}
      <div className="flex justify-between mb-12">
        {/* 閉じるボタン (左寄せ) */}
        <button
          className="bg-white text-black border border-gray-300 py-2 px-4 rounded"
          onClick={handleCloseClick}
        >
          閉じる
        </button>

        {/* 下書き保存ボタンと公開ボタン (右寄せ) */}
        <div className="flex space-x-4">
          {/* 新規作成または下書きを編集する場合下書き保存ボタンを表示する */}
          {(!blogStatus || blogStatus === "DRAFT") && (
            <button
              className="bg-white text-black border border-gray-300 py-2 px-4 rounded"
              onClick={() => {
                handleDraftSave();
              }}
            >
              下書き保存
            </button>
          )}
          <button
            className="bg-white text-black border border-gray-300 py-2 px-4 rounded"
            onClick={() => {
              handleSave();
            }}
          >
            {/* 公開済みのブログを編集する場合ボタン名を「更新」にする */}
            {blogStatus && blogStatus !== "DRAFT" ? "更新" : "公開"}
          </button>
        </div>
      </div>
      {/* サムネイル画像追加パート */}
      <>
        <div
          className="relative w-10/12 mx-auto py-8" // 10/12のサイズで中央寄せ
        >
          {croppedImage ? (
            <img
              src={croppedImage}
              alt="選択されたプロフィール画像"
              className="w-full object-contain cursor-pointer" // 縦横比16:9を保持しつつ表示
              onClick={triggerImageSelect}
            />
          ) : thumbnailUrl ? (
            <>
              <img
                src={`${config.apiBaseUrl}/api/public/files/${thumbnailUrl}`}
                alt="選択されたプロフィール画像"
                className="w-full object-contain cursor-pointer" // 縦横比16:9を保持しつつ表示
                onClick={triggerImageSelect}
              />
            </>
          ) : (
            <div
              className="flex items-center justify-center w-16 h-16 bg-gray-200 rounded-full sm:-ml-12 cursor-pointer"
              onClick={triggerImageSelect}
            >
              <i className="fa-regular fa-image fa-2x rounded-fulltext-blue-300 items-center"></i>
            </div>
          )}

          {/* アイコンを中央に表示 */}
          <div
            className={`absolute inset-0 flex items-center justify-center ${croppedImage || thumbnailUrl ? "cursor-pointer" : ""}`}
            onClick={
              croppedImage || thumbnailUrl ? triggerImageSelect : undefined
            }
          >
            <i className="fa-regular fa-image fa-2x text-white"></i>{" "}
            {/* カメラアイコン */}
          </div>
        </div>
        {selectedImage && (
          <>
            <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-75 z-50">
              <div className="relative w-full max-w-md mx-auto p-4 bg-white shadow-lg">
                <div className="relative w-full h-64">
                  <Cropper
                    image={selectedImage}
                    crop={crop}
                    zoom={zoom}
                    aspect={16 / 9} // 固定アスペクト比
                    onCropChange={setCrop}
                    onZoomChange={setZoom}
                    onCropComplete={onCropComplete}
                  />
                </div>
                <div className="flex justify-center mt-4">
                  <button
                    type="button"
                    className="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded"
                    onClick={showCroppedImage}
                  >
                    画像を切り取る
                  </button>
                </div>
              </div>
            </div>
          </>
        )}
        <input
          id="profileImageInput"
          type="file"
          className="hidden"
          onChange={handleImageChange}
        />
      </>

      {/* タイトル入力欄とカテゴリ選択欄 */}
      <div className="flex flex-col sm:flex-row sm:items-center mb-4">
        <div className="flex-grow sm:mr-4 mb-4 sm:mb-0">
          {/* タイトルが未入力の場合にエラーメッセージを表示 */}
          {titleError && (
            <p className="text-red-500 mb-2">タイトルが未入力です。</p>
          )}
          <textarea
            id="title"
            value={title}
            onChange={handleTitleChange}
            className={`w-full py-2 px-3 text-gray-700 resize-none text-4xl ${
              titleError ? "border border-red-500" : ""
            }`}
            placeholder="記事タイトル"
            maxLength={50}
          />{" "}
        </div>

        <div className="flex-none w-full sm:w-48">
          <label
            htmlFor="category"
            className="block text-gray-700 font-bold mb-2 text-right"
          >
            カテゴリ
          </label>
          <select
            id="category"
            value={category}
            onChange={handleCategoryChange}
            className="border rounded w-full py-2 px-3 text-gray-700"
          >
            {blogCategories.map((category) => (
              <option key={category.code} value={category.code}>
                {category.displayName}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Tiptapエディタを表示 */}
      {content ? (
        <TiptapEditor content={content} ref={editorRef} />
      ) : (
        <TiptapEditor ref={editorRef} />
      )}

      {/* 関連アーティストの表示 */}
      <div className="mb-4">
        {relatedArtists.length > 0 || showArtistDropdown ? (
          <div>
            <h3 className="text-gray-700 font-bold mb-4">関連アーティスト</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {relatedArtists.map((artist) => (
                <div
                  key={artist.id}
                  className="bg-gray-100 rounded-lg p-2 flex items-center justify-between"
                >
                  <div className="flex items-center">
                    <img
                      src={artist.imageUrl}
                      alt={artist.name}
                      className="w-12 h-12 rounded-full mr-4"
                    />
                    <span className="text-gray-700 text-lg">{artist.name}</span>
                  </div>
                  <button
                    className="text-red-500 hover:text-red-700"
                    onClick={() => handleRemoveArtist(artist.id)}
                  >
                    &times;
                  </button>
                </div>
              ))}
            </div>

            <p className="text-gray-500 text-sm mt-6">
              ※ログインユーザの好きなアーティストのみ選択可能です。
            </p>
            {/* アーティスト選択肢が1件以上ある場合表示 */}
            {availableArtistOptions.length > 0 && (
              <div className="relative">
                <div className="bg-white border rounded mt-2 w-full max-h-64 overflow-y-auto">
                  {availableArtistOptions.map((artist) => (
                    <div
                      key={artist.id}
                      onClick={() => handleAddArtist(artist.id)}
                      className="flex items-center px-4 py-2 cursor-pointer hover:bg-gray-100"
                    >
                      <img
                        src={artist.imageUrl}
                        alt={artist.name}
                        className="w-10 h-10 rounded-full mr-2"
                      />
                      <span>{artist.name}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        ) : (
          // 関連アーティストが未登録の場合追加ボタンを表示
          <button
            className="bg-blue-500 text-white py-2 px-4 rounded"
            onClick={() => setShowArtistDropdown(true)}
          >
            関連アーティストを追加する
          </button>
        )}
      </div>
      {/* セットリスト */}
      <div className="mb-4">
        {/* 本編が0件の場合非表示 */}
        {setlist.length !== 0 ? (
          <>
            <div className="flex items-center mb-2">
              <h3 className="text-gray-700 font-bold mr-2">セットリスト</h3>
              <button
                className="bg-gray-300 w-10 h-8 text-white py-1 px-2 m-2 rounded"
                onClick={() => setShowSetlist(!showSetlist)}
              >
                {showSetlist ? "-" : "+"}
              </button>
              {/* エラーが一件でもある場合はエラーメッセージを表示 */}
              {setlistErrors.some((error) => error) && (
                <p className="text-red-500 ml-4">穴あき入力はできません。</p>
              )}
            </div>
            {showSetlist && (
              <>
                <table className="table-fixed w-full mb-4 border-collapse">
                  <thead>
                    <tr>
                      <th className="border px-4 py-2 w-2/12">No.</th>
                      <th className="border px-4 py-2 w-10/12">曲名</th>
                    </tr>
                  </thead>
                  <tbody>
                    {setlist.map((track, index) => (
                      <tr key={index}>
                        <td className="border px-4 py-2 text-center">
                          {index + 1}
                        </td>
                        <td className="border px-4 py-2">
                          <input
                            type="text"
                            value={track.trackName}
                            onChange={(e) =>
                              handleTrackChange(index, e.target.value)
                            }
                            className={`w-full py-1 px-2 border rounded ${
                              setlistErrors[index] ? "border-red-500" : ""
                            }`} // エラーの場合赤枠を適用
                            placeholder="曲名を入力"
                            maxLength={50}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {/* 追加ボタン  曲数が上限の場合メッセージを表示*/}
                {setlist.length < MAX_SETLIST_SONGS ? (
                  <button
                    onClick={addMoreTracks}
                    className="bg-blue-500 text-white py-2 px-4 rounded mr-4"
                  >
                    曲数を追加
                  </button>
                ) : (
                  <p className="text-red-500">
                    追加できる曲数は{MAX_SETLIST_SONGS}曲までです。
                  </p>
                )}

                {/* アンコール枠 */}
                {encoreSections.length === 0 && (
                  <button
                    onClick={addEncoreSection}
                    className="bg-green-500 text-white py-2 px-4 rounded"
                  >
                    アンコール枠を追加
                  </button>
                )}
                {encoreSections && (
                  <>
                    {encoreSections.map((encore, encoreIndex) => (
                      <div key={encoreIndex} className="my-4">
                        <div className="flex items-center">
                          <h3 className="text-gray-700 font-bold mb-2 mr-4">
                            en {encoreIndex + 1}
                          </h3>
                          {/* セクション全体がエラーの場合と一部がエラーの場合でメッセージを切り替える */}
                          {encoreSectionErrors[encoreIndex].every(
                            (error) => error
                          ) ? (
                            <p className="text-red-500">
                              空のセクションは登録できません。
                            </p>
                          ) : encoreSectionErrors[encoreIndex].some(
                              (error) => error
                            ) ? (
                            <p className="text-red-500">
                              穴あき入力はできません。
                            </p>
                          ) : null}{" "}
                        </div>
                        <table className="table-fixes w-full mb-4 border-collapse">
                          <thead>
                            <tr>
                              <th className="border px-4 py-2 w-2/12">No.</th>
                              <th className="border px-4 py-2 w-10/12">曲名</th>
                            </tr>
                          </thead>
                          <tbody>
                            {encore.map((track, index) => (
                              <tr key={index}>
                                <td className="border px-4 py-2 text-center">
                                  {index + 1}
                                </td>
                                <td className="border px-4 py-2">
                                  <input
                                    type="text"
                                    value={track.trackName}
                                    onChange={(e) =>
                                      handleTrackChange(
                                        index,
                                        e.target.value,
                                        true,
                                        encoreIndex
                                      )
                                    }
                                    className={`w-full py-1 px-2 border rounded ${
                                      encoreSectionErrors[encoreIndex] &&
                                      encoreSectionErrors[encoreIndex][index]
                                        ? "border-red-500"
                                        : ""
                                    }`} // エラーの場合赤枠を適用
                                    placeholder="曲名を入力"
                                    maxLength={50}
                                  />
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>

                        {encore.length < MAX_ENCORE_SONGS ? (
                          <button
                            onClick={() => addMoreTracks(true, encoreIndex)}
                            className="bg-blue-500 text-white py-2 px-4 rounded mr-6"
                          >
                            曲数を追加
                          </button>
                        ) : (
                          <p className="text-red-500">
                            追加できる曲数は{MAX_ENCORE_SONGS}曲までです。
                          </p>
                        )}

                        {/* 新しいアンコール枠を追加 */}
                        {encoreIndex + 1 >= MAX_ENCORE_SECTIONS ? (
                          <>
                            <button
                              onClick={removeEncoreSection}
                              className="bg-red-500 text-white py-2 px-4 rounded"
                            >
                              アンコール枠を削除
                            </button>

                            <p className="text-red-500">
                              追加できるアンコール数は{MAX_ENCORE_SECTIONS}
                              つまでです。
                            </p>
                          </>
                        ) : encoreIndex + 1 === encoreSections.length ? (
                          <>
                            <button
                              onClick={addEncoreSection}
                              className="bg-green-500 text-white py-2 px-4 rounded mr-4"
                            >
                              アンコール枠を追加
                            </button>
                            <button
                              onClick={removeEncoreSection}
                              className="bg-red-500 text-white py-2 px-4 mt-4 rounded"
                            >
                              アンコール枠を削除
                            </button>
                          </>
                        ) : (
                          <></>
                        )}
                      </div>
                    ))}
                  </>
                )}
              </>
            )}
          </>
        ) : (
          <button
            onClick={() => {
              setShowSetlist(true);
              addMoreTracks();
            }}
            className="bg-blue-500 text-white py-2 px-4 rounded"
          >
            セットリストを追加する
          </button>
        )}
      </div>
      {/* 確認モーダル */}
      {isModalOpen && (
        <ConfirmModal
          message="入力内容が破棄されますがよろしいですか？"
          onConfirm={handleConfirm}
          onCancel={handleCancel}
        />
      )}
    </>
  );
}
