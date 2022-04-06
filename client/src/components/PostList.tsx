import React from 'react';
import IPostList from "../interfaces/IPostList";
import PostItem from "./PostItem";
import AppButton from "./UI/button/AppButton";

const PostList = ({postList, name}: IPostList) => {
    return (
        <div className="post_list">
            <div className="post_list_header">
                <h1>Список оптимизаций пользователя {name}</h1>
            </div>
            <div className="post_list_add_btn">
                <AppButton>Add post</AppButton>
            </div>
            {
                postList.map((post) =>
                    <PostItem post={post.post} key={post.post.id}/>
                )
            }
        </div>
    );
};

export default PostList;