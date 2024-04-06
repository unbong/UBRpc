package io.unbong.ubrpc.core.cluster;

import io.unbong.ubrpc.core.api.Router;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-03 22:29
 */
@Slf4j
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;

    public int getGrayRatio() {
        return grayRatio;
    }

    public void setGrayRatio(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    private Random random = new Random();

    /**
     * 用
     * @param providers
     * @return
     */
    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {

        if(providers == null  || providers.size() <= 1){
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        // classification
        providers.forEach(provider->{
            if("true".equals(provider.getParameters().get("gray")))
            {
                grayNodes.add(provider);
            }
            else{
                normalNodes.add(provider);
            }
        });

        log.debug("gray nodes:{}, normal nodes:{}", grayNodes, normalNodes);

        if(normalNodes.isEmpty() || grayNodes.isEmpty())
        {
            log.debug("all node return.");
            return providers;
        }

        if(grayRatio <= 0 )
        {
            log.debug("gray ratio is {}. return normal nodes. {}", grayRatio, normalNodes);
            return normalNodes;

        }

        else if(grayRatio >= 100)
        {
            log.debug("gray ratio is {} . return gray nodes. {}", grayRatio, grayNodes);
            return  grayNodes;
        }

        else
        {
            if(random.nextInt(100) <= grayRatio){
                log.debug("gray router-> gary nodes. {}", grayNodes);
                return grayNodes;
            }
            else{
                log.debug("gray router-> nodes{}", normalNodes);
                return normalNodes;
            }
        }
    }
}
