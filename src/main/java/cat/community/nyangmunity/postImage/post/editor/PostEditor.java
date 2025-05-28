package cat.community.nyangmunity.postImage.post.editor;

import lombok.Getter;

@Getter
public class PostEditor {
	private final String content;

	public PostEditor(String content) {
		this.content = content;
	}

	public static PostEditorBuilder builder() {
		return new PostEditorBuilder();
	}

	public static class PostEditorBuilder {
		private String content;

		PostEditorBuilder() {
		}

		public PostEditorBuilder content(final String content) {
			if (content != null) {
				this.content = content;
			}
			return this;
		}

		public PostEditor build() {
			return new PostEditor(this.content);
		}

		public String toString() {
			return "PostEditor.PostEditorBuilder(content=" + this.content + ")";
		}
	}
}
