package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import android.net.Uri
import com.example.findmypet.common.Resource
import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class UploadImagesUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend operator fun invoke(imageUris: List<Uri>): Resource<List<String>> {
        return try {
            when (val uploadResult = postRepository.uploadImagesAndGetDownloadUrls(imageUris)) {
                is Resource.Success -> Resource.Success(uploadResult.data)
                is Resource.Error -> uploadResult
                is Resource.Loading -> Resource.Loading
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
