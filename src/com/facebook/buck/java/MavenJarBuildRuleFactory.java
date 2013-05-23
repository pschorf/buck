package com.facebook.buck.java;

import com.facebook.buck.parser.AbstractBuildRuleFactory;
import com.facebook.buck.parser.BuildRuleFactoryParams;
import com.facebook.buck.parser.NoSuchBuildTargetException;
import com.facebook.buck.rules.AbstractBuildRuleBuilder;

public class MavenJarBuildRuleFactory extends AbstractBuildRuleFactory {

  @Override
  protected AbstractBuildRuleBuilder newBuilder() {
    return MavenJarRule.newMavenJarRuleBuilder();
  }

  @Override
  protected void amendBuilder(AbstractBuildRuleBuilder abstractBuilder, BuildRuleFactoryParams params)
      throws NoSuchBuildTargetException {
      MavenJarRule.Builder builder = ((MavenJarRule.Builder)abstractBuilder);
      builder.setVersion(params.getRequiredStringAttribute("version"));
      builder.setArtifact(params.getRequiredStringAttribute("artifact"));
      builder.setGroup(params.getRequiredStringAttribute("group"));
  }

}
