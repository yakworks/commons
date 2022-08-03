package yakworks.api.problem;

import yakworks.api.AsMap;
import yakworks.message.MsgKey;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
public interface Violation extends AsMap {

    MsgKey getMsg();

    default String getCode() {
        return getMsg() != null ? getMsg().getCode() : null;
    }

    default String getField(){ return null; }

    default String getMessage() {
        return null;
    }

    @Override
    default Map<String, Object> asMap(){
        Map<String, Object> hmap = new HashMap<>();
        hmap.put("code", getCode());
        hmap.put("field", getField());
        hmap.put("message", getMessage());
        return hmap;
    }
}
