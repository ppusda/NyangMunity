package cat.community.NyangMunity.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import cat.community.NyangMunity.board.entity.BoardPet;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardPet is a Querydsl query type for BoardPet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoardPet extends EntityPathBase<BoardPet> {

    private static final long serialVersionUID = 1963864559L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardPet boardPet = new QBoardPet("boardPet");

    public final QBoard board;

    public final QPet pet;

    public QBoardPet(String variable) {
        this(BoardPet.class, forVariable(variable), INITS);
    }

    public QBoardPet(Path<? extends BoardPet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardPet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardPet(PathMetadata metadata, PathInits inits) {
        this(BoardPet.class, metadata, inits);
    }

    public QBoardPet(Class<? extends BoardPet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board"), inits.get("board")) : null;
        this.pet = inits.isInitialized("pet") ? new QPet(forProperty("pet"), inits.get("pet")) : null;
    }

}

