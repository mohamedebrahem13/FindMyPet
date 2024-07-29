package com.example.findmypet.common

object Constant {
    const val ID = "id"
    const val E_MAIL = "email"
    const val NICKNAME = "nickname"
    const val PHONE_NUMBER = "phone"
    const val PROFILE_IMAGE_PATH = "Profile_image"
    const val IMAGE_PASS ="imagePath"


    const val COLLECTION_PATH = "users"
    const val POSTS="posts"
    const val IMAGES = "images"
    const val USERID="user.id"
    const val POST_COUNT= "postCount"

    //Image fields
    const val URL = "url"
    const val CREATED_AT = "createdAt"

    //File names
    const val PROFILE_IMAGE_NAME = ".jpg"

    const val FAVORITE="favoritePosts"

    const val CHAT_CHANNEL_ID = "chat_channel"
    const val CHAT_CHANNEL_NAME = "Chat Notifications"
    const val CHAT_CHANNEL_DESCRIPTION = "Notifications for chat messages"

    const val PET_CHANNEL_ID = "new_pet_channel"
    const val PET_CHANNEL_NAME = "New Pet"
    const val TOPIC = "/topics/new_pet"


    const val PET_CHANNEL_DESCRIPTION = "Notifications for new pets"
    const val BASE_URL="https://fcm.googleapis.com"
    const val CONTENT_TYPE="application/json"
    const val ADDITIONAL_TEXT = "There is a new post added: "
    const val PAGE_SIZE=5




}