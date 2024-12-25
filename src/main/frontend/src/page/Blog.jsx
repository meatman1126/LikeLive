import React, { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import BlogEditor from "../component/BlogEditor";
import BlogViewer from "../component/BlogViewer";
import Header from "../component/Header";

/**
 *
 * @param {*} isEdit 編集有無を指定する（新規作成、編集の場合true）
 */
export default function Blog({ isAuthenticated, setIsAuthenticated, isEdit }) {
  // 編集モード有無を保持する
  const [isEditMode, setIsEditMode] = useState(isEdit);
  // 編集または閲覧対象のブログID
  const { targetBlogId } = useParams();

  const location = useLocation();
  const showComments = location.state?.showComments || false;

  // isEditが変更されるたびに、isEditModeを更新
  useEffect(() => {
    setIsEditMode(isEdit);
  }, [isEdit]);

  return (
    <>
      <Header
        isAuthenticated={isAuthenticated}
        setIsAuthenticated={setIsAuthenticated}
      />

      {isEditMode ? (
        <div className="p-12">
          {targetBlogId ? (
            // 編集対象のブログが指定されている場合
            <BlogEditor targetBlogId={targetBlogId} />
          ) : (
            // 編集対象が指定されていない場合は新規作成
            <BlogEditor />
          )}
        </div>
      ) : (
        // 編集モードではない場合は閲覧
        <div className="p-12">
          <BlogViewer
            isAuthenticated={isAuthenticated}
            targetBlogId={targetBlogId}
            showComments={showComments}
          />
        </div>
      )}
    </>
  );
}
