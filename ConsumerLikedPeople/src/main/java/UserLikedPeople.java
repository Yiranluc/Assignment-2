public class UserLikedPeople {
  private String swiper;
  private String[] likedPeople;

  public UserLikedPeople(String swiper, String[] likedPeople) {
    this.swiper = swiper;
    this.likedPeople = likedPeople;
  }

  @Override
  public String toString() {
    return "UserLikedPeople{" +
        "swiper='" + swiper + '\'' +
        '}';
  }
}
