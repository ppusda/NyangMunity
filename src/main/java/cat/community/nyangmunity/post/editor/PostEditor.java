package cat.community.nyangmunity.post.editor;

import lombok.Getter;

@Getter
public class PostEditor {
    private final String content;

    public PostEditor(String content) {
        this.content = content;
    }

    public static PostEditor.BoardEditorBuilder builder() {
        return new PostEditor.BoardEditorBuilder();
    }

    public static class BoardEditorBuilder {
        private String content;

        BoardEditorBuilder() {
        }

        public PostEditor.BoardEditorBuilder content(final String content) {
            if(content != null){
                this.content = content;
            }
            return this;
        }

        public PostEditor build() {
            return new PostEditor(this.content);
        }

        public String toString() {
            return "BoardEditor.BoardEditorBuilder(content=" + this.content + ")";
        }
    }
}
