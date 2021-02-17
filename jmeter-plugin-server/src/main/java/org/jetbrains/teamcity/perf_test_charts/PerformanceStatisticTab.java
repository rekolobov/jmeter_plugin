package org.jetbrains.teamcity.perf_test_charts;

import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.metadata.BuildMetadataEntry;
import jetbrains.buildServer.serverSide.metadata.MetadataStorage;
import jetbrains.buildServer.serverSide.statistics.ValueProviderRegistry;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.teamcity.PluginConstants;
import org.jetbrains.teamcity.perf_tests.PerformanceBuildMetadataProvider;
import org.jetbrains.teamcity.perf_tests.PerformanceTestProvider;
import org.jetbrains.teamcity.perf_tests.PerformanceTestRun;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class PerformanceStatisticTab extends SimpleCustomTab {
	private static final String resourceURL = "statistic/tabPerfStat.jsp";

	private final ModelAndView myGraphHelper;
	private final ValueProviderRegistry myRegistry;
	private final SBuildServer myServer;
	private final BuildDataStorage myStorage;
	private final MetadataStorage myMetadataStorage;


	private final PerformanceTestProvider myPerformanceTestHolder;

	public PerformanceStatisticTab(@NotNull final PagePlaces pagePlaces, @NotNull final SBuildServer server, final BuildDataStorage storage,
	                               @NotNull final ValueProviderRegistry valueProviderRegistry,
	                               @NotNull final PerformanceTestProvider testHolder, @NotNull final PluginDescriptor descriptor, @NotNull MetadataStorage metadataStorage) {
		super(pagePlaces, PlaceId.BUILD_RESULTS_TAB, "performanceTests",  descriptor.getPluginResourcesPath(resourceURL), "Performance Statistics");
		myServer = server;
		myRegistry = valueProviderRegistry;
		myStorage = storage;
		myGraphHelper = new ModelAndView(" /buildGraph.html?jsp=" + descriptor . getPluginResourcesPath ( resourceURL ));
		myPerformanceTestHolder = testHolder;
		myMetadataStorage = metadataStorage;

		addCssFile("/css/buildGraph.css");

		addJsFile(descriptor.getPluginResourcesPath("flot/jquery.flot.js"));
		addJsFile(descriptor.getPluginResourcesPath("flot/excanvas.js"));
		addJsFile(descriptor.getPluginResourcesPath("flot/jquery.flot.selection.js"));
		addJsFile(descriptor.getPluginResourcesPath("flot/jquery.flot.time.min.js"));

		addJsFile(descriptor.getPluginResourcesPath("statistic/perfChartsCustom.js"));

		register();
	}

	@Override
	public boolean isAvailable(@NotNull final HttpServletRequest request) {
		SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
		return build != null && build.getBuildType() != null && !build.getBuildType().getBuildFeaturesOfType(PluginConstants.FEATURE_TYPE_AGGREGATION).isEmpty();
	}

	public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
		request.setAttribute("buildGraphHelper", myGraphHelper);

		SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
		model.put("deselectedSeries", build.getBuildType().getCustomDataStorage(PluginConstants.STORAGE_ID_DEFAULT_DESELECTED_SERIES).getValues());


		Collection<PerformanceTestRun> successTests = myPerformanceTestHolder.getSuccessTestRuns(build);

		for (Iterator<BuildMetadataEntry> it = myMetadataStorage.getBuildEntry(build.getBuildId(), PerformanceBuildMetadataProvider.PERFORMANCE_META_DATA_PROVIDER_ID); it.hasNext();)  {
			BuildMetadataEntry entry = it.next();
			if (PerformanceBuildMetadataProvider.KEY_META_DATA_WARNINGS.equals(entry.getKey())) {
				Map<String, String> warnings = entry.getMetadata();
				if (!warnings.isEmpty()) {
					for (PerformanceTestRun test : successTests) {
						String testID = test.getFullName();
						if (warnings.containsKey(testID)) {
							test.setWarning(warnings.get(testID));
						}
					}
				}
			}
		}

		model.put("performanceOKTests", successTests);
		model.put("performanceFailedTests", myPerformanceTestHolder.getFailedTestRuns(build));

		model.put("allTestNames", myPerformanceTestHolder.getAllTestNames(build));
		model.put("allTestGroups", myPerformanceTestHolder.getAllThreadGroups(build));

		model.put("build", build);
		model.put("statistic", build.getFullStatistics());
		model.put("isShowResponseCodes", build.getParametersProvider().get(PluginConstants.PARAMS_HTTP_RESPONSE_CODE) == null ? false : build.getParametersProvider().get(PluginConstants.PARAMS_HTTP_RESPONSE_CODE));
		model.put("isLogSaved", myPerformanceTestHolder.isLogAvailable(build));
	}
}
