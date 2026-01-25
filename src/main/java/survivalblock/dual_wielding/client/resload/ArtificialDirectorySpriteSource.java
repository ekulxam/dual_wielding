package survivalblock.dual_wielding.client.resload;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import survivalblock.dual_wielding.common.DualWieldingUnbound;
import survivalblock.dual_wielding.mixin.client.particle.AtlasSourceManagerAccessor;

import java.math.BigInteger;
import java.util.List;

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
        OffhandSweepParticleTextureCreator.INSTANCE.onResourceManagerReload(rm);

        for (ResourceLocation id : textureIds) {
            ResourceMetadata metadata = ResourceMetadata.EMPTY;
            OffhandSweepParticleTextureCreator.ImageAndMetadata imageAndMetadata = OffhandSweepParticleTextureCreator.INSTANCE.images.get(id);
            NativeImage nativeImage = imageAndMetadata.image();
            if (nativeImage == null) {
                return;
            }
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();



            AnimationMetadataSection animationMetadataSection = imageAndMetadata.metadata().getSection(AnimationMetadataSection.SERIALIZER)
                    .orElse(AnimationMetadataSection.EMPTY);
            FrameSize frameSize = animationMetadataSection.calculateFrameSize(nativeImage.getWidth(), nativeImage.getHeight());

            ResourceLocation idWithoutDirectory = id.withPath(
                    string ->
                            string.replace("textures/particle/", "")
                                    .replace(".png", "")
            );

            if (Mth.isMultipleOf(nativeImage.getWidth(), frameSize.width()) && Mth.isMultipleOf(nativeImage.getHeight(), frameSize.height())) {
                output.add(
                        id,
                        spriteResourceLoader -> new SpriteContents(
                                idWithoutDirectory,
                                frameSize,
                                nativeImage,
                                metadata
                        )
                );
            } else {
                DualWieldingUnbound.LOGGER.error(
                        "Image {} size {},{} is not multiple of frame size {},{}",
                        id,
                        nativeImage.getWidth(),
                        nativeImage.getHeight(),
                        frameSize.width(),
                        frameSize.height()
                );
                nativeImage.close();
            }
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
}
