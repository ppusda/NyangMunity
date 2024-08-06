package cat.community.NyangMunity.board.editor;

import lombok.Getter;

@Getter
public class BoardEditor {
    private final String content;

    public BoardEditor(String content) {
        this.content = content;
    }

    public static BoardEditor.BoardEditorBuilder builder() {
        return new BoardEditor.BoardEditorBuilder();
    }

    public static class BoardEditorBuilder {
        private String content;

        BoardEditorBuilder() {
        }

        public BoardEditor.BoardEditorBuilder content(final String content) {
            if(content != null){
                this.content = content;
            }
            return this;
        }

        public BoardEditor build() {
            return new BoardEditor(this.content);
        }

        public String toString() {
            return "BoardEditor.BoardEditorBuilder(content=" + this.content + ")";
        }
    }
}
