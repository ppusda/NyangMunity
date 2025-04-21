package cat.community.nyangmunity.member.editor;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class MemberEditor {
	private final String nickname;
	private final String password;
	private final LocalDate birthday;

	public MemberEditor(String nickname, String password, LocalDate birthday) {
		this.nickname = nickname;
		this.password = password;
		this.birthday = birthday;
	}

	public static MemberEditor.UserEditorBuilder builder() {
		return new MemberEditor.UserEditorBuilder();
	}

	public static class UserEditorBuilder {
		private String nickname;
		private String password;
		private LocalDate birthday;

		UserEditorBuilder() {
		}

		public MemberEditor.UserEditorBuilder nickname(final String nickname) {
			if (nickname != null) {
				this.nickname = nickname;
			}
			return this;
		}

		public MemberEditor.UserEditorBuilder password(final String password) {
			if (password != null) {
				this.password = password;
			}
			return this;
		}

		public MemberEditor.UserEditorBuilder birthday(final LocalDate birthday) {
			if (birthday != null) {
				this.birthday = birthday;
			}
			return this;
		}

		public MemberEditor build() {
			return new MemberEditor(this.nickname, this.password, this.birthday);
		}

		@Override
		public String toString() {
			return "UserEditor.UserEditorBuilder{" +
				"nickname='" + this.nickname + '\'' +
				", password='" + this.password + '\'' +
				", birthday=" + this.birthday +
				'}';
		}
	}
}
