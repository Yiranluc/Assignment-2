public class UserLikes {
  private String swiper;
  private int likes;
  private int dislikes;

  public UserLikes(String swiper, int likes, int dislikes) {
    this.swiper = swiper;
    this.likes = likes;
    this.dislikes = dislikes;
  }

  @Override
  public String toString() {
    return "UserLikes{" +
        "swiper='" + swiper + '\'' +
        ", likes=" + likes +
        ", dislikes=" + dislikes +
        '}';
  }
}
