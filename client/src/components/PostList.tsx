import React from 'react';
import IPostList from "../interfaces/IPostList";
import PostItem from "./PostItem";

const PostList = ({postList, name}: IPostList) => {
    return (
        <div>
            <div className="post_list_header">
                <h1>Список оптимизаций пользователя {name}</h1>
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