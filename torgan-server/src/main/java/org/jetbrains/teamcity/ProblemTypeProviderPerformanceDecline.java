package org.jetbrains.teamcity;

import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.problems.BaseBuildProblemTypeDetailsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProblemTypeProviderPerformanceDecline extends BaseBuildProblemTypeDetailsProvider {

	@Nullable
	public String getStatusText(@NotNull final BuildProblemData buildProblem, @NotNull final SBuild build) {
		return PluginConstants.CRITICAL_PERFORMANCE_PROBLEM_TYPE;
	}

	@NotNull
	@Override
	public String getType() {
		return PluginConstants.CRITICAL_PERFORMANCE_PROBLEM_TYPE;
	}

	@Nullable
	@Override
	public String getTypeDescription() {
		return PluginConstants.CRITICAL_PERFORMANCE_PROBLEM_TYPE;
	}
}
