package cat.community.NyangMunity.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetBoard is a Querydsl query type for PetBoard
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPetBoard extends EntityPathBase<PetBoard> {

    private static final long serialVersionUID = 808272541L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetBoard petBoard = new QPetBoard("petBoard");

    public final QBoard board;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPet pet;

    public QPetBoard(String variable) {
        this(PetBoard.class, forVariable(variable), INITS);
    }

    public QPetBoard(Path<? extends PetBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetBoard(PathMetadata metadata, PathInits inits) {
        this(PetBoard.class, metadata, inits);
    }

    public QPetBoard(Class<? extends PetBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board"), inits.get("board")) : null;
        this.pet = inits.isInitialized("pet") ? new QPet(forProperty("pet"), inits.get("pet")) : null;
    }

}

