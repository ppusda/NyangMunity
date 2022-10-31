package cat.community.NyangMunity.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetRank is a Querydsl query type for PetRank
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPetRank extends EntityPathBase<PetRank> {

    private static final long serialVersionUID = -1636031083L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetRank petRank = new QPetRank("petRank");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPetBoard petBoard;

    public QPetRank(String variable) {
        this(PetRank.class, forVariable(variable), INITS);
    }

    public QPetRank(Path<? extends PetRank> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetRank(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetRank(PathMetadata metadata, PathInits inits) {
        this(PetRank.class, metadata, inits);
    }

    public QPetRank(Class<? extends PetRank> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.petBoard = inits.isInitialized("petBoard") ? new QPetBoard(forProperty("petBoard"), inits.get("petBoard")) : null;
    }

}

