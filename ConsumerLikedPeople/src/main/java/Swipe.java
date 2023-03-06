public class Swipe {
  private String swiper;
  private String swipee;
  private String comment;

  public Swipe(String swiper, String swipee, String comment) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  public String getSwiper() {
    return swiper;
  }

  @Override
  public String toString() {
    return "Swipe{" +
        "swiper='" + swiper + '\'' +
        ", swipee='" + swipee + '\'' +
        ", comment='" + comment + '\'' +
        '}';
  }
}
