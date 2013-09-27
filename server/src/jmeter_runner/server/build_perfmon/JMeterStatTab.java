package jmeter_runner.server.build_perfmon;

import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import jmeter_runner.common.JMeterPluginConstants;
import jmeter_runner.server.build_perfmon.data_providers.PerfmonDataProvider;
import jmeter_runner.server.build_perfmon.data_providers.ResultsDataProvider;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class JMeterStatTab extends SimpleCustomTab {
	private File perfmonArtifact;
	private File logArtifact;

	protected final SBuildServer myServer;

	public JMeterStatTab(@NotNull PagePlaces pagePlaces, @NotNull final SBuildServer server) {
		super(pagePlaces, PlaceId.BUILD_RESULTS_TAB, "jmeter", JMeterPluginConstants.PERFMON_STATISTIC_TAB_JSP, "JMeterPerfMon");
		myServer = server;
		register();
	}

	@Override
	public boolean isAvailable(@NotNull final HttpServletRequest request) {
		setArtifactFiles(request);
		return logArtifact != null;
	}

	public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
		Collection<Graph> data = new ArrayList<Graph>();
		if (logArtifact != null) {
			data = new ResultsDataProvider(logArtifact).getData();
		}
		if (perfmonArtifact != null) {
			PerfmonDataProvider p1 = new PerfmonDataProvider(perfmonArtifact);
			data.addAll(p1.getData());
			model.put("hostName", p1.getHostName());
		}
		SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
		if (build != null) {
			setState(data, build.getBuildType());
		}
		model.put("metrics", data);
		model.put("build", build);
	}

	private void setArtifactFiles(@NotNull HttpServletRequest request) {
		perfmonArtifact = null;
		logArtifact = null;
		final SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
		if (build != null) {
			for (File artifact : build.getArtifactsDirectory().listFiles()) {
				String absPath = artifact.getAbsolutePath();
				if (absPath.contains("perfmon"))  {
					perfmonArtifact = artifact;
				}
				if (absPath.contains(".jtl"))  {
					logArtifact = artifact;
				}
			}
		}
	}

	private void setState(Collection<Graph> graphs, SBuildType buildType) {
		for (Graph graph : graphs) {
			String state = buildType.getParameters().get(graph.getId());
			graph.setState(state == null ? "shown" : state);
		}
	}

}