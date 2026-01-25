package survivalblock.dual_wielding.client.resload;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.Nullable;
import survivalblock.dual_wielding.common.DualWieldingUnbound;
import survivalblock.dual_wielding.mixin.client.particle.AtlasSourceManagerAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
public class ArtificialDirectorySpriteSource implements SpriteSource {

    public static final MapCodec<ArtificialDirectorySpriteSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            ResourceLocation.CODEC.listOf().fieldOf("textureIds").forGetter(artificialDirectory -> artificialDirectory.textureIds)
                            )
                    .apply(instance, ArtificialDirectorySpriteSource::new)
    );
    public static final SpriteSourceType TYPE = registerType(DualWieldingUnbound.id("artificial_directory"), CODEC);

    private final List<ResourceLocation> textureIds;

    public ArtificialDirectorySpriteSource(List<ResourceLocation> textureIds) {
        this.textureIds = textureIds;
    }

    @Override
    public void run(ResourceManager rm, Output output) {
        /*
        if (!(rm instanceof MultiPackResourceManager resourceManager)) {
            DualWieldingUnbound.LOGGER.warn("Unable to properly load offhand sweep particle, resource manager is {}", rm);
            return;
        }
         */
        OffhandSweepParticleTextureCreator.INSTANCE.onResourceManagerReload(rm);
        /*
        ResourceLocation particleAtlas = ParticleManagerAccessor.dual_wielding$getParticleAtlasPath();
        FallbackResourceManager trueManager = ((LifecycledResourceManagerImplAccessor) resourceManager).dual_wielding$getSubManagers().get(particleAtlas.getNamespace());
        trueManager.push(new Resources());
         */
        for (ResourceLocation id : textureIds) {
            ResourceMetadata metadata = ResourceMetadata.EMPTY;
            NativeImage nativeImage = OffhandSweepParticleTextureCreator.INSTANCE.images.get(id);
            if (nativeImage == null) {
                return;
            }
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();
            int greatestCommonFactor;
            {
                BigInteger bWidth = BigInteger.valueOf(width);
                BigInteger bHeight = BigInteger.valueOf(height);
                greatestCommonFactor = bWidth.gcd(bHeight).intValueExact();
            }
            FrameSize frameSize = new FrameSize(greatestCommonFactor, greatestCommonFactor);

            output.add(
                    id,
                    spriteResourceLoader -> new SpriteContents(
                            id.withPath(
                                    string ->
                                            string.replace("textures/particle/", "")
                                                    .replace(".png", "")
                            ),
                            frameSize,
                            nativeImage,
                            metadata)
            );
        }
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }

    public static SpriteSourceType registerType(ResourceLocation id, MapCodec<? extends SpriteSource> codec) {
        SpriteSourceType spriteSourceType = new SpriteSourceType(codec);
        SpriteSourceType spriteSourceType2 = AtlasSourceManagerAccessor.dual_wielding$getTypes().putIfAbsent(id, spriteSourceType);
        if (spriteSourceType2 != null) {
            throw new IllegalStateException("Duplicate registration " + id);
        } else {
            return spriteSourceType;
        }
    }

    public class Resources implements PackResources {

        @Override
        public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
            return null;
        }

        @Override
        public @Nullable IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
            return null;
        }

        @Override
        public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        }

        @Override
        public Set<String> getNamespaces(PackType type) {
            return ArtificialDirectorySpriteSource.this.textureIds.stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
        }

        @Override
        public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
            return null;
        }

        @Override
        public PackLocationInfo location() {
            return new PackLocationInfo("", Component.empty(), PackSource.BUILT_IN, Optional.empty());
        }

        @Override
        public void close() {

        }
    }
}
