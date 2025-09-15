#!/bin/bash

# Validate Nexus deployment configuration
# This script checks that all required variables are set and Maven configuration is valid

set -e

echo "Validating Nexus deployment configuration..."

# Check required environment variables
required_vars=("NEXUS_URL" "NEXUS_USERNAME" "NEXUS_PASSWORD")
for var in "${required_vars[@]}"; do
    if [[ -z "${!var}" ]]; then
        echo "❌ ERROR: $var environment variable is not set"
        exit 1
    else
        echo "✅ $var is set"
    fi
done

# Validate NEXUS_URL format
if [[ ! "$NEXUS_URL" =~ ^https?:// ]]; then
    echo "❌ ERROR: NEXUS_URL must start with http:// or https://"
    exit 1
fi

echo "✅ NEXUS_URL format is valid: $NEXUS_URL"

# Test Maven property resolution
echo "🔍 Testing Maven property resolution..."
nexus_resolved=$(mvn help:evaluate -Dexpression=nexus.url -q -DforceStdout)
if [[ "$nexus_resolved" != "$NEXUS_URL" ]]; then
    echo "❌ ERROR: Maven property resolution failed. Expected: $NEXUS_URL, Got: $nexus_resolved"
    exit 1
fi

echo "✅ Maven property resolution works correctly"

# Test Maven settings configuration
echo "🔍 Testing Maven settings..."
if [[ -f ~/.m2/settings.xml ]]; then
    echo "✅ Maven settings.xml found"
    if grep -q "nexus-releases" ~/.m2/settings.xml && grep -q "nexus-snapshots" ~/.m2/settings.xml; then
        echo "✅ Nexus server configurations found in settings.xml"
    else
        echo "⚠️  WARNING: Nexus server configurations not found in settings.xml"
    fi
else
    echo "⚠️  WARNING: No Maven settings.xml found"
fi

# Test build with nexus-deploy profile
echo "🔍 Testing build with nexus-deploy profile..."
mvn clean package -DskipTests -Pnexus-deploy -q

if [[ $? -eq 0 ]]; then
    echo "✅ Build with nexus-deploy profile successful"
    
    # Check if all expected artifacts are created
    artifacts=("target/*.jar" "target/*-sources.jar" "target/*-javadoc.jar")
    for artifact_pattern in "${artifacts[@]}"; do
        if ls $artifact_pattern 1> /dev/null 2>&1; then
            echo "✅ Artifacts found: $(ls $artifact_pattern)"
        else
            echo "❌ ERROR: No artifacts found matching $artifact_pattern"
            exit 1
        fi
    done
else
    echo "❌ ERROR: Build with nexus-deploy profile failed"
    exit 1
fi

echo ""
echo "🎉 All validations passed! Nexus deployment configuration is ready."
echo ""
echo "Next steps:"
echo "1. Ensure your Nexus server is accessible from GitHub Actions runners"
echo "2. Configure the required secrets in your GitHub repository:"
echo "   - NEXUS_URL: $NEXUS_URL"
echo "   - NEXUS_USERNAME: (your Nexus username)"  
echo "   - NEXUS_PASSWORD: (your Nexus password/token)"
echo "3. Push to main branch or create a version tag to trigger deployment"