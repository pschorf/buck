package com.facebook.buck.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;

import com.facebook.buck.rules.AbstractCachingBuildRule;
import com.facebook.buck.rules.AbstractCachingBuildRuleBuilder;
import com.facebook.buck.rules.BuildContext;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.BuildRuleType;
import com.facebook.buck.rules.CachingBuildRuleParams;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.Step;
import com.facebook.buck.util.BuckConstant;
import com.google.common.collect.ImmutableList;

public class MavenJarRule extends AbstractCachingBuildRule {

  private final String group;
  private final String artifact;
  private final String version;

  MavenJarRule(CachingBuildRuleParams cachingBuildRuleParams, String group, String artifact, String version) {
    super(cachingBuildRuleParams);
    this.group = group;
    this.version = version;
    this.artifact = artifact;
  }

  private String getJarPath() {
    return String.format("%s/%s/%s-%s.jar", BuckConstant.GEN_DIR, group, artifact, version);
  }

  @Override
  protected Iterable<String> getInputsToCompareToOutput(BuildContext context) {
    return ImmutableList.of(getJarPath());
  }

  @Override
  protected List<Step> buildInternal(BuildContext context) throws IOException {
    Step step = new Step() {

      @Override
      public int execute(ExecutionContext context) {
        URL jar;
        try {
          jar = new URL(String.format("http://search.maven.org/remotecontent?filepath=%s/%s/%s/%s-%s.jar", group,
              artifact, version, artifact, version));
        } catch (MalformedURLException e) {
          e.printStackTrace();
          return -1;
        }
        try {
          new File(getJarPath()).getParentFile().mkdir();
          ReadableByteChannel rbc = Channels.newChannel(jar.openStream());
          try (FileOutputStream os = new FileOutputStream(getJarPath())) {
            os.getChannel().transferFrom(rbc, 0, 1 << 24);
          }
        } catch (IOException e) {
          e.printStackTrace();
          return -1;
        }
        return 0;
      }

      @Override
      public String getShortName(ExecutionContext context) {
        return String.format("maven %s:%s:%s", group, artifact, version);
      }

      @Override
      public String getDescription(ExecutionContext context) {
        return getShortName(context);
      }
    };
    return ImmutableList.of(step);
  }

  @Override
  public BuildRuleType getType() {
    return BuildRuleType.MAVEN_JAR;
  }

  public static Builder newMavenJarRuleBuilder() {
    return new Builder();
  }

  public static class Builder extends AbstractCachingBuildRuleBuilder {

    private Builder() {}

    private String artifact;
    private String group;
    private String version;

    public Builder setArtifact(String artifact) {
      this.artifact = artifact;
      return this;
    }

    public Builder setGroup(String group) {
      this.group = group;
      return this;
    }

    public Builder setVersion(String version) {
      this.version = version;
      return this;
    }

    @Override
    public MavenJarRule build(Map<String, BuildRule> buildRuleIndex) {
      return new MavenJarRule(createCachingBuildRuleParams(buildRuleIndex),
          group,
          artifact,
          version);
    }

  }
}
