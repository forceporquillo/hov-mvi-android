package dev.forcecodes.hov.domain.usecase.details

import dev.forcecodes.hov.core.Result
import dev.forcecodes.hov.core.UiState
import dev.forcecodes.hov.core.internal.Logger
import dev.forcecodes.hov.core.model.DetailsUiModel
import dev.forcecodes.hov.core.qualifiers.IoDispatcher
import dev.forcecodes.hov.domain.mapper.DetailsUiMapper
import dev.forcecodes.hov.domain.source.BasicInfo
import dev.forcecodes.hov.domain.source.DetailsRepository
import dev.forcecodes.hov.domain.usecase.BaseFlowUseCase
import dev.forcecodes.hov.domain.usecase.UseCaseParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserDetailsUseCase @Inject constructor(
    private val detailsUiMapper: DetailsUiMapper,
    private val detailsRepository: DetailsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : BaseFlowUseCase<GetUserDetailsUseCase.Params, DetailsViewState>(dispatcher) {

    class Params(val id: Int, val name: String) : UseCaseParams.Params()

    override fun execute(parameters: Params): Flow<DetailsViewState> {
        return detailsRepository.getUserDetails(
            BasicInfo(parameters.id, parameters.name)
        ).map { result ->
            when (result) {
                is Result.Success -> {
                    DetailsViewState(
                        data = detailsUiMapper.invoke(result.data),
                        isLoading = false,
                        error = null
                    )
                }

                is Result.Error -> {
                    Logger.e("Error: ${result.exception}")
                    DetailsViewState(
                        data = detailsUiMapper.invoke(result.data),
                        isLoading = false,
                        error = result.exception.message
                    )
                }

                is Result.Loading -> {
                    val viewState = detailsUiMapper.invoke(result.data)
                    DetailsViewState(
                        data = viewState,
                        isLoading = viewState == null,
                        error = null
                    )
                }
            }
        }
    }

    override fun mapExceptionToResult(params: Throwable): DetailsViewState {
        return DetailsViewState(false, params.message, null)
    }
}

data class DetailsViewState(
    val isLoading: Boolean,
    val error: String?,
    val data: DetailsUiModel?
) : UiState {

    companion object {
        fun initial() = DetailsViewState(
            data = null,
            isLoading = true,
            error = null
        )
    }
}