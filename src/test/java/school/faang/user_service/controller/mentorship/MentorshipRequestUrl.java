package school.faang.user_service.controller.mentorship;

public enum MentorshipRequestUrl {
  URL_ADD("/mentorship/add"),
  URL_REJECT("/mentorship/reject/{id}"),
  URL_LIST("/mentorship/list"),
  URL_ACCEPT("/mentorship/accept/{id}");

  final String url;

  MentorshipRequestUrl(String url) {
    this.url = url;
  }
}
